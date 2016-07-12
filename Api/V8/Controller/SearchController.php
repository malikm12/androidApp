<?php

namespace SuiteCRM\Api\V8\Controller;

use Slim\Http\Request as Request;
use Slim\Http\Response as Response;
use SuiteCRM\Api\Core\Api;
use SuiteCRM\Api\V8\Library\SearchLib;

class SearchController extends Api
{
    public function getSearchResults(Request $req, Response $res, $args)
    {
        global $container;

        //$data = $req->getParsedBody();
        $data = $req->getParams();

        if (!isset($data['query_string'])) {
            return $this->generateResponse($res, 400, 'Incorrect parameters', 'Failure');
        } else {
            //if ($container['jwt'] !== null && $container['jwt']->userId !== null) {
                $lib = new SearchLib();
                $results = $lib->getSearchResults(1);

                return $this->generateResponse($res, 200, $results, 'Success');
            //} else {
            //    return $this->generateResponse($res, 401, 'No user id', 'Failure');
            //}
        }
    }
}
