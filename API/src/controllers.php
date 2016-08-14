<?php
/**
 * Controllers file handles redirection and logic to control API responses
 *
 * Author:             Ashton Herrington
 * Last Modified Date: 04-23-16
 */
namespace Google\Cloud\Samples\Bookshelf;

use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Google\Cloud\Samples\Bookshelf\DataModel\Datastore;

$userAttributes    = ['firstName', 'userName', 'password', 'school'];
$notesetAttributes = ['userId', 'school', 'title'];
$noteAttributes    = ['notesetId', 'frontText', 'backText'];

/**
 * Adds user to the Google Datastore
 *
 * Constraints:
 * Required fields: firstName, userName, password, school
 */
$app->post('/adduser', function (Request $request) use ($app, $userAttributes) {
    /** @var Datastore $model */
    $model = $app['flashcard.model'];

    $entity         = array();
    $requiredFields = array();

    foreach ($userAttributes as $attribute) {
        $entity[$attribute] = $request->get($attribute);
        if (is_null($entity[$attribute])) {
            $requiredFields[] = $attribute;
        }
    }
    if (!empty($requiredFields)) {
        $missingFields = implode(', ', $requiredFields);
        $response = new Response(
            json_encode(
                array(
                    'status' => 'Error',
                    'message' => "Fields $missingFields required"))
        );
        $response->setStatusCode(400);
        return $response;
    }

    $datastore = new Datastore('homeworktwo-1272');
    $existingId = $datastore->findUser($entity['userName'], $entity['password'], 'User');
    if (!is_null($existingId)) {
        $response = new Response(
            json_encode(
                array(
                    'status' => 'Error',
                    'message' => "Username taken"))
        );
        $response->setStatusCode(500);
        return $response;
    }

    if ($id = $model->create($entity, 'User')) {
        $response = new Response(
            json_encode(
                array(
                'status'  => 'Success',
                'message' => "User created",
                'id'      => "$id"))
        );
        $response->setStatusCode(200);
        return $response;
    }

    $response = new Response(
        json_encode(
            array(
            'status' => 'Error',
            'message' => "User creation failed"))
    );
    $response->setStatusCode(500);
    return $response;
});

/**
 * Adds a noteset to the Google Datastore
 *
 * Constraints:
 * Required fields: userId, school, title
 * userId must match the ID of an existing user
 */
$app->post('/addnoteset', function (Request $request) use ($app, $notesetAttributes) {
    /** @var Datastore $model */
    $model = $app['flashcard.model'];

    $entity         = array();
    $requiredFields = array();

    foreach ($notesetAttributes as $attribute) {
        $entity[$attribute] = $request->get($attribute);
        if (is_null($entity[$attribute])) {
            $requiredFields[] = $attribute;
        }
    }
    if (!empty($requiredFields)) {
        $missingFields = implode(', ', $requiredFields);
        $response = new Response(
            json_encode(
                array(
                    'status' => 'Error',
                    'message' => "Fields $missingFields required"))
        );
        $response->setStatusCode(400);
        return $response;
    }

    $datastore = new Datastore('homeworktwo-1272');

    $userEntity = $datastore->read('User', $request->get('userId'));

    if (!$userEntity) {
        $response = new Response(
            json_encode(
                array(
                    'status'  => 'Error',
                    'message' => "userId not found"))
        );
        $response->setStatusCode(400);
        return $response;
    }
    
    if ($id = $model->create($entity, 'Noteset')) {
        $response = new Response(
            json_encode(
                array(
                    'status'  => 'Success',
                    'message' => "Noteset created",
                    'id'      => "$id"))
        );
        $response->setStatusCode(200);
        return $response;
    }

    $response = new Response(
        json_encode(
            array(
                'status' => 'Error',
                'message' => "Noteset creation failed"))
    );
    $response->setStatusCode(500);
    return $response;
});

/**
 * Adds a note entity to the Google Datastore
 *
 * Constraints:
 * Required fields: notesetId, frontText, backText
 * notesetId must match an existing notesetId
 */
$app->post('/addnote', function (Request $request) use ($app, $noteAttributes) {
    /** @var Datastore $model */
    $model = $app['flashcard.model'];

    $entity         = array();
    $requiredFields = array();

    foreach ($noteAttributes as $attribute) {
        $entity[$attribute] = $request->get($attribute);
        if (is_null($entity[$attribute])) {
            $requiredFields[] = $attribute;
        }
    }
    if (!empty($requiredFields)) {
        $missingFields = implode(', ', $requiredFields);
        $response = new Response(
            json_encode(
                array(
                    'status' => 'Error',
                    'message' => "Fields $missingFields required"))
        );
        $response->setStatusCode(400);
        return $response;
    }

    $datastore = new Datastore('homeworktwo-1272');

    $userEntity = $datastore->read('Noteset', $request->get('notesetId'));

    if (!$userEntity) {
        $response = new Response(
            json_encode(
                array(
                    'status'  => 'Error',
                    'message' => "notesetId not found"))
        );
        $response->setStatusCode(400);
        return $response;
    }

    if ($id = $model->create($entity, 'Note')) {
        $response = new Response(
            json_encode(
                array(
                    'status'  => 'Success',
                    'message' => "Note created",
                    'id'      => "$id"))
        );
        $response->setStatusCode(200);
        return $response;
    }

    $response = new Response(
        json_encode(
            array(
                'status' => 'Error',
                'message' => "Note creation failed"))
    );
    $response->setStatusCode(500);
    return $response;
});

/**
 * Edits a user entity
 *
 * Constraints:
 * Required fields: firstName, userName, password, school
 * Username and password must both exist and be from same entity
 */
$app->put('/edituser', function (Request $request) use ($app, $userAttributes) {
    /** @var Datastore $model */
    $model = $app['flashcard.model'];

    $entity = array();
    $requiredFields = array();

    foreach ($userAttributes as $attribute) {
        $entity[$attribute] = $request->get($attribute);
        if (is_null($entity[$attribute])) {
            $requiredFields[] = $attribute;
        }
    }

    if (!empty($requiredFields)) {
        $missingFields = implode(', ', $requiredFields);
        $response = new Response(
            json_encode(
                array(
                    'status' => 'Error',
                    'message' => "Fields $missingFields required"))
        );
        $response->setStatusCode(400);
        return $response;
    }

    $entity['id'] = $model->findUser($entity['userName'], $entity['password'], 'User');

    if (is_null($entity['id']) || $entity['id'] == -1) {
        $response = new Response(
            json_encode(
                array(
                    'status' => 'Error',
                    'message' => "Username|password combination invalid",
                    'id'       => $entity['id']))
        );
        $response->setStatusCode(400);
        return $response;
    }

    $datastore = new Datastore('homeworktwo-1272');
    $datastore->update($entity, 'User');

    $response = new Response(
        json_encode(
            array(
                'status' => 'Success',
                'message' => "User updated"))
    );
    $response->setStatusCode(200);
    return $response;
});

/**
 * Provides login services for user entities.
 *
 * Constraints:
 * Required fields: userName and password
 * Username and password must both exist and be from same entity
 */
$app->post('/login/{userName}/{password}', function (Request $request, $userName, $password) use ($app) {
    /** @var Datastore $model */
    $model = $app['flashcard.model'];

    //$userName = $request->get('userName');
    //$password = $request->get('password');
    if (is_null($userName) || is_null($password)) {
        $response = new Response(
            json_encode(
                array(
                    'status' => 'Error',
                    'message' => "userName and password required"))
        );
        $response->setStatusCode(400);
        return $response;
    }

    $token = $request->query->get('page_token');
    $id = $model->findUser($userName, $password, 'User', $token);

    if (is_null($id) || $id == -1) {
        $response = new Response(
            json_encode(array('status' => 'Error', 'message' => "Username|password combination invalid"))
        );
        $response->setStatusCode(400);
        return $response;
    }

    $response = new Response(
        json_encode(array('status' => 'Success', 'message' => "$id"))
    );
    $response->setStatusCode(200);
    return $response;

});

/**
 * Retrieves notes related to a specific noteset
 *
 * Constraints:
 * Required fields: notesetId
 * A noteset with the notesetId must exist
 */
$app->get('/getnotes/{notesetId}', function (Request $request, $notesetId) use ($app, $userAttributes) {
    /** @var Datastore $model */
    $model = $app['flashcard.model'];

    if (is_null($notesetId)) {
        $response = new Response(
            json_encode(array('status' => 'Error', 'message' => "notesetId required"))
        );
        $response->setStatusCode(400);
        return $response;
    }

    $results = $model->findRelated($notesetId, 'Note');

    $response = new Response(json_encode($results));
    $response->setStatusCode(200);
    return $response;

});

/**
 * Retrieves notesets related to a specific user
 *
 * Constraints:
 * Required fields: userId
 * A user with the userId must exist
 */
$app->get('/getnoteset/{userId}', function (Request $request, $userId) use ($app, $userAttributes) {
    /** @var Datastore $model */
    $model = $app['flashcard.model'];

    if (is_null($userId)) {
        $response = new Response(
            json_encode(array('status' => 'Error', 'message' => "userId required"))
        );
        $response->setStatusCode(400);
        return $response;
    }

    $results = $model->findRelated($userId, 'Noteset');

    $response = new Response(json_encode($results));
    $response->setStatusCode(200);
    return $response;

});

/**
 * Updates an existing note entity
 *
 * Constraints:
 * Required fields: notesetId, frontText, backText
 * notesetId must remain the same - cannot be altered
 */
$app->put('/updatenote', function (Request $request) use ($app, $noteAttributes) {
    /** @var Datastore $model */
    $model = $app['flashcard.model'];

    $entity = array();
    $requiredFields = array();

    foreach ($noteAttributes as $attribute) {
        $entity[$attribute] = $request->get($attribute);
        if (is_null($entity[$attribute])) {
            $requiredFields[] = $attribute;
        }
    }
    if (!empty($requiredFields)) {
        $missingFields = implode(', ', $requiredFields);
        $response = new Response(
            json_encode(
                array(
                    'status' => 'Error',
                    'message' => "Fields $missingFields required"))
        );
        $response->setStatusCode(400);
        return $response;
    }
    $entity['id'] = $request->get('id');

    if ($valid = $model->confirmNotesetId($entity['id'], $entity['notesetId'])) {
       
        $datastore = new Datastore('homeworktwo-1272');
        $datastore->update($entity, 'Note');

        $response = new Response(
            json_encode(
                array(
                    'status' => 'Success',
                    'message' => "Note updated"))
        );
        $response->setStatusCode(200);
        return $response;
    }
    $response = new Response(
        json_encode(
            array(
                'status' => 'Error',
                'message' => "Cannot alter notesetId"))
    );
    $response->setStatusCode(400);
    return $response;
});

/**
 * Deletes a note specified by ID in URL
 *
 * Costraints:
 * Required fields: noteId
 * note id must exist in Datastore
 */
$app->delete('/delete/note/{id}', function (Request $request, $id) use ($app) {
    /** @var Datastore $model */
    $model = $app['flashcard.model'];

    $model->delete('Note', $id);

    $response = new Response(
        json_encode(
            array(
                'status' => 'Success',
                'message' => "Note $id deleted"))
    );
    $response->setStatusCode(200);
    return $response;
});

/**
 * Deletes a noteset specified by ID in URL, all its related notes are also deleted
 *
 * Costraints:
 * Required fields: notesetId
 * noteset id must exist in Datastore
 */
$app->delete('/delete/noteset/{id}', function (Request $request, $id) use ($app) {
    /** @var Datastore $model */
    $model = $app['flashcard.model'];

    $relatedNotes = $model->findRelated($id, 'Note');

    foreach($relatedNotes as $noteId => $noteEntity) {
        $deleteDatastore = new Datastore('homeworktwo-1272');
        $deleteDatastore->delete('Note', $noteId);
    }
    $model->delete('Noteset', $id);

    $response = new Response(
        json_encode(
            array(
                'status' => 'Success',
                'message' => "Noteset $id and all associated notes deleted"))
    );
    $response->setStatusCode(200);
    return $response;
});

/**
 * Deletes a user specified by ID in URL, all its related notesets and notes are also deleted
 *
 * Costraints:
 * Required fields: userid
 * user id must exist in Datastore
 */
$app->delete('/delete/user/{id}', function (Request $request, $id) use ($app) {
    /** @var Datastore $model */
    $model = $app['flashcard.model'];

    $relatedNotesets = $model->findRelated($id, 'Noteset');

    foreach($relatedNotesets as $notesetId => $notesetEntity) {

        $findDatastore = new Datastore('homeworktwo-1272');
        $relatedNotes = $findDatastore->findRelated($notesetId, 'Note');
        foreach($relatedNotes as $noteId => $noteEntity) {
            $deleteDatastore = new Datastore('homeworktwo-1272');
            $deleteDatastore->delete('Note', $noteId);
        }
        $deleteDatastore = new Datastore('homeworktwo-1272');
        $deleteDatastore->delete('Noteset', $notesetId);
    }
    $deleteDatastore = new Datastore('homeworktwo-1272');
    $deleteDatastore->delete('User', $id);

    $response = new Response(
        json_encode(
            array(
                'status' => 'Success',
                'message' => "User $id, associated notesets, and notes deleted "))
    );
    $response->setStatusCode(200);
    return $response;
});





