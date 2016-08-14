<?php
/**
 * This file/project was originally part of a Google Datastore howto guide known as "2-structured-data", it was used
 * as a template, and heavily modified to meet the needs of my assignments
 *
 * Author:             Ashton Herrington
 * Last Modified Date: 04-23-16
 */
namespace Google\Cloud\Samples\Bookshelf\DataModel;

use Google_Service_Datastore;
use Symfony\Component\HttpFoundation\Response;

/**
 * Class Datastore provides the methods for creating, accessing and deleting User/Noteset/Note entities.
 */
class Datastore
{
    //attributes user entity must contain
    protected $userAttributes    = [
        'userName'      => 'string',
        'firstName'     => 'string',
        'password'      => 'string',
        'school'        => 'string',
    ];
    //attributes noteset entity must contain
    protected $notesetAttributes = [
        'userId'        => 'string',
        'school'        => 'string',
        'title'         => 'string',
    ];
    //attributes note entity must contain
    protected $noteAttributes    = [
        'notesetId'     => 'string',
        'frontText'     => 'string',
        'backText'      => 'string',
    ];

    /**
     * Datastore constructor. This is boilerplate code acquired from a Google Datastore howto package known as
     * "2-strucutured-data"
     *
     * @param integer $datastoreDatasetId datastore dataset id
     *
     * @return Datastore
     */
    public function __construct($datastoreDatasetId)
    {
        $this->datasetId = $datastoreDatasetId;
        // Datastore API has intermittent failures, so we set the
        // Google Client to retry in the event of a 503 Backend Error
        $retryConfig = [ 'retries' => 2 ];
        $client = new \Google_Client([ 'retry' => $retryConfig ]);
        $client->setScopes([
            Google_Service_Datastore::CLOUD_PLATFORM,
            Google_Service_Datastore::DATASTORE,
            // @TODO: remove this scope when we move to Datastore v3
            Google_Service_Datastore::USERINFO_EMAIL,
        ]);
        $client->setDeveloperKey('AIzaSyA_Gb4sOgEaDg7aLojYQQWcPEhu8ZMWfAU');
        $client->useApplicationDefaultCredentials();
        $this->datastore = new \Google_Service_Datastore($client);
    }

    /**
     * Create method creates an entity in the Datastore using a local representation of an entity, the type of entity
     * to create, and the optional addition to set its key.
     *
     * @param $entity    entity representation
     * @param $type      type of the entity
     * @return mixed     returns the id of the newly created entity
     * @throws \Exception
     */
    public function create($entity, $type)
    {
        $this->verifyColumns($entity, $type);
        
        $key = $this->createKey($type);
        
        $attributes = null;
        if ($type == 'User') {
            $attributes = $this->userAttributes;
        } else if ($type == 'Noteset') {
            $attributes = $this->notesetAttributes;
        } else if ($type == 'Note') {
            $attributes = $this->noteAttributes;
        } else {
            throw new \Exception('Invalid type');
        }

        $properties = $this->toProperties($entity, $attributes);

        $datastoreEntity = new \Google_Service_Datastore_Entity([
            'key' => $key,
            'properties' => $properties
        ]);

        // Use "NON_TRANSACTIONAL" for simplicity (as we're only making one call)
        $request = new \Google_Service_Datastore_CommitRequest([
            'mode' => 'NON_TRANSACTIONAL',
            'mutation' => [
                'insertAutoId' => [$datastoreEntity]
            ]
        ]);

        $response = $this->datastore->datasets->commit($this->datasetId, $request);

        $keys = $response->getMutationResult()->getInsertAutoIdKeys();

        // return the ID of the created datastore item
        return $keys[0]->getPath()[0]->getId();
    }

    /**
     * Finds a user of the entity type "type" specified by their username and password
     *
     * @param $username    users username
     * @param $password    users password
     * @param $type        type of entity, always user (@todo: remove this)
     * @return int|null
     * @throws \Exception
     */
    public function findUser($username, $password, $type)
    {
        $query = new \Google_Service_Datastore_Query([
            'kinds' => [
                [
                    'name' => "$type",
                ],
            ],
        ]);

        $attributes = null;
        if ($type == 'User') {
            $attributes = $this->userAttributes;
        } else if ($type == 'Noteset') {
            $attributes = $this->notesetAttributes;
        } else if ($type == 'Note') {
            $attributes = $this->noteAttributes;
        } else {
            throw new \Exception('Invalid type');
        }

        $request = new \Google_Service_Datastore_RunQueryRequest();
        $request->setQuery($query);
        $response = $this->datastore->datasets->
            runQuery($this->datasetId, $request);

        /** @var \Google_Service_Datastore_QueryResultBatch $batch */
        $batch = $response->getBatch();
        
        foreach ($batch->getEntityResults() as $entityResult) {
            $entity = $entityResult->getEntity();
            $singleEntry = $this->toEntity($entity->getProperties(), $attributes);
            if ($singleEntry['userName'] == $username && $singleEntry['password'] == $password ){
                return $entity->getKey()->getPath()[0]->getId();
            } else if ($singleEntry['userName'] == $username) {
                return -1;
            }
        }
        return null;
    }

    /**
     * Confirms that the provided ID and notesetID are matching within the Datastore entity
     * 
     * @param $id        ID of the note
     * @param $notesetId ID of the noteset
     * @return int       boolean returned
     */
    public function confirmNotesetId($id, $notesetId)
    {
        $query = new \Google_Service_Datastore_Query([
            'kinds' => [
                [
                    'name' => "Note",
                ],
            ],
        ]);
        $attributes = $this->noteAttributes;
        $request = new \Google_Service_Datastore_RunQueryRequest();
        $request->setQuery($query);
        $response = $this->datastore->datasets->runQuery($this->datasetId, $request);

        /** @var \Google_Service_Datastore_QueryResultBatch $batch */
        $batch = $response->getBatch();
        
        foreach ($batch->getEntityResults() as $entityResult) {
            $entity = $entityResult->getEntity();
            $singleEntry = $this->toEntity($entity->getProperties(), $attributes);
            if ($entity->getKey()->getPath()[0]->getId() == $id && $singleEntry['notesetId'] == $notesetId) {
                return 1;
            }
        }
        return 0;
    }

    /**
     * Finds all entities related to the entity (either notesets related to user, or notes related to noteset)
     * 
     * @param $idToRelate ID of the entity you wish to find the children of 
     * @param $type       the type of the children requested
     * @return array
     * @throws \Exception
     */
    public function findRelated($idToRelate, $type)
    {
        $query = new \Google_Service_Datastore_Query([
            'kinds' => [
                [
                    'name' => "$type",
                ],
            ],
        ]);

        $attributes = null;
        if ($type == 'User') {
            $attributes = $this->userAttributes;
        } else if ($type == 'Noteset') {
            $attributes = $this->notesetAttributes;
        } else if ($type == 'Note') {
            $attributes = $this->noteAttributes;
        } else {
            throw new \Exception('Invalid type');
        }

        $request = new \Google_Service_Datastore_RunQueryRequest();
        $request->setQuery($query);
        $response = $this->datastore->datasets->runQuery($this->datasetId, $request);

        /** @var \Google_Service_Datastore_QueryResultBatch $batch */
        $batch = $response->getBatch();

        $parentId = null;
        if ($type == 'Noteset') {
            $parentId = 'userId';
        } else if ($type == 'Note') {
            $parentId = 'notesetId';
        } else {
            throw new \Exception('Invalid type');
        }

        $results = array();
        foreach ($batch->getEntityResults() as $entityResult) {
            $entity = $entityResult->getEntity();
            $singleEntry = $this->toEntity($entity->getProperties(), $attributes);
            if ($singleEntry[$parentId] == $idToRelate){
                if ($type == 'Note') {
                    $results[$entity->getKey()->getPath()[0]->getId()] =
                        array(
                            'frontText' => $singleEntry['frontText'],
                            'backText'  => $singleEntry['backText'],
                            'notesetId' => $singleEntry['notesetId'],
                            );
                } else if ($type == 'Noteset') {
                    $results[$entity->getKey()->getPath()[0]->getId()] =
                        array(
                            'school' => $singleEntry['school'],
                            'title'  => $singleEntry['title'],
                            'userId' => $singleEntry['userId'],
                        );
                }
            }
        }
        return $results;
    }

    /**
     * Method updates an entity with new information
     * 
     * @param $entity entity representation
     * @param $type   type of the entity
     * @return void   
     * 
     * @throws InvalidArgumentException
     * @throws \Exception
     */
    public function update($entity, $type)
    {
        $this->verifyColumns($entity, $type, true);
        
        if (!isset($entity['id'])) {
            throw new InvalidArgumentException('Entity must have an "id" attribute');
        }

        $attributes = null;
        if ($type == 'User') {
            $attributes = $this->userAttributes;
        } else if ($type == 'Noteset') {
            $attributes = $this->notesetAttributes;
        } else if ($type == 'Note') {
            $attributes = $this->noteAttributes;
        } else {
            throw new \Exception('Invalid type');
        }

        $key = $this->createKey($type, $entity['id']);
        unset($entity['id']);

        $properties = $this->toProperties($entity, $attributes);

        $datastoreEntity = new \Google_Service_Datastore_Entity([
            'key' => $key,
            'properties' => $properties
        ]);
        
        // Use "NON_TRANSACTIONAL" for simplicity (as we're only making one call)
        $request = new \Google_Service_Datastore_CommitRequest([
            'mode' => 'NON_TRANSACTIONAL',
            'mutation' => [
                'update' => [$datastoreEntity]
            ]
        ]);
        
        $this->datastore->datasets->commit($this->datasetId, $request);
    }

    /**
     * Reads the information about an entity and returns it in array representation
     * 
     * @param $type       type of entity requested
     * @param $id         ID of the entity requested
     * @return array|bool
     * @throws \Exception
     */
    public function read($type, $id)
    {
        $key = $this->createKey($type, $id);
        $request = new \Google_Service_Datastore_LookupRequest([
            'keys' => [$key]
        ]);

        $attributes = null;
        if ($type == 'User') {
            $attributes = $this->userAttributes;
        } else if ($type == 'Noteset') {
            $attributes = $this->notesetAttributes;
        } else if ($type == 'Note') {
            $attributes = $this->noteAttributes;
        } else {
            throw new \Exception('Invalid type');
        }

        $response = $this->datastore->datasets->lookup($this->datasetId, $request);

        /** @var \Google_Service_Datastore_QueryResultBatch $batch */
        if ($found = $response->getFound()) {
            $entity = $this->toEntity($found[0]['entity']['properties'], $attributes);
            $entity['id'] = $id;

            return $entity;
        }
        return false;
    }
    
    /**
     * Removes a single entity from the Datastore
     * 
     * @param $type type of entity to remove
     * @param $id   ID of the entity to remove
     * @return void
     */
    public function delete($type, $id)
    {
        $key = $this->createKey($type, $id);

        // Use "NON_TRANSACTIONAL" for simplicity (as we're only making one call)
        $request = new \Google_Service_Datastore_CommitRequest([
            'mode' => 'NON_TRANSACTIONAL',
            'mutation' => [
                'delete' => [$key]
            ]
        ]);
        $this->datastore->datasets->commit($this->datasetId, $request);
    }

    /**
     * Creates a Datastore key to use in transactions. This was taken from the boilerplate code and modified to create
     * keys for multiple entity types.
     * 
     * @param $type    type of entity you wish to work with
     * @param null $id
     * @return \Google_Service_Datastore_Key
     */
    protected function createKey($type, $id = null)
    {
        $key = new \Google_Service_Datastore_Key([
            'path' => [
                [
                    'kind' => "$type"
                ],
            ]
        ]);
        // If we have an ID, set it in the path
        if ($id) {
            $key->getPath()[0]->setId($id);
        }
        return $key;
    }

    /**
     * Verifies the columns contain all necessary fields (specific to type)
     * 
     * @param $entity     entity to work with
     * @param $type       type of the entity
     * @param bool $useId if userId, add id to attributes list
     * @throws \Exception
     */
    private function verifyColumns($entity, $type, $useId = false)
    {
        $attributes = null;
        if ($type == 'User') {
            $attributes = $this->userAttributes;
        } else if ($type == 'Noteset') {
            $attributes = $this->notesetAttributes;
        } else if ($type == 'Note') {
            $attributes = $this->noteAttributes;
        } else {
            throw new \Exception('Invalid type');
        }
        if ($useId){
            $attributes['id'] = 'id';
        }

        if ($invalid = array_diff_key($entity, $attributes)) {
            throw new \InvalidArgumentException(sprintf(
                'unsupported column properties: "%s"',
                implode(', ', array_keys($entity))
            ));
        }
    }

    /**
     * Converts an entity representation to an array of its properties. This was taken from the boilerplate code.
     * 
     * @param array $entity     entity represetation
     * @param array $attributes attributes specific to the entity type
     * @return array
     */
    private function toProperties(array $entity, array $attributes)
    {
        $properties = [];
        foreach ($entity as $colName => $colValue) {
            $propName = $attributes[$colName] . 'Value';
            $properties[$colName] = [
                 $propName => $colValue
            ];
        }

        return $properties;
    }
    
    /**
     * Converts an array of properties to an entity representation. This was taken from the boilerplate code.
     *
     * @param array $properties array of entity properties
     * @param array $attributes attributes specific to the entity type
     * @return array
     */
    private function toEntity(array $properties, array $attributes)
    {
        $object = [];
        foreach ($attributes as $colName => $colType) {
            $object[$colName] = null;
            if (isset($properties[$colName])) {
                $propName = $colType . 'Value';
                $object[$colName] = $properties[$colName][$propName];
            }
        }
        return $object;
    }
}
