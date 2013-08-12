/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package edu.msu.nscl.olog;

/**
 * TODO
 * 1. The absence of the propertiesResource similar to logbooksResource and 
 * TagsResource. 
 * The key, value pairing makes it different from simple tags/logbooks but we
 * might like the ability to 
 * - retrieve a list of all properties
 * - delete a property completely from all logs
 * - add/delete a property to a single log 
 * all these can be implemented simply copying the tagsResources
 * 
 * the operations which include setting the property value for multiple logs
 * is what will require additional work.
 */

/**
 * Service Description
 * 
 * .../logbooks
 * GET      retrieving the list of logbooks in the database
 * POST     creating multiple logbooks
 * 
 * .../logbooks/<logbookName>
 * GET      retrieve one logbook
 * PUT      create/replace once logbook
 * POST     update a logbook
 * DELETE   delete one logbook
 * 
 * .../logbooks/<logbookName>/<logid>
 * PUT      add logbook link to the log
 * DELETE   delete logbook link from log
 * 
 * 
 *  .../tags
 * GET      retrieving the list of tags in the database
 * POST     creating multiple tags
 * 
 * .../tags/<tagName>
 * GET      retrieve one tagName
 * PUT      create/replace once tag
 * POST     update a tag
 * DELETE   delete one tag
 * 
 * .../tags/<tagName>/<logid>
 * PUT      add tag to a log
 * DELETE   delete a tag from a log
 * 
 * 
 * MISSSING
 *  .../properties
 * GET      retrieving the list of properties in the database
 * POST     creating multiple properties(property value in payload)
 * 
 * MISSSING
 * .../properties/<propertyName>
 * GET      retrieve one propertyName
 * PUT      create/replace once property(property value in payload)
 * POST     update a property(property value in payload)
 * DELETE   delete one property
 * 
 * MISSSING
 * .../properties/<propertyName>/<logid>
 * PUT      add property to a log
 * DELETE   delete a property from a log
 * 
 * 
 * .../logs
 * POST     create multiple log instances
 * GET      query/retrieving a collection of Log instances
 * 
 * .../logs/<logId>
 * GET      retrieve single log
 * PUT      create/replace a single log
 * POST     update a single log
 * DELETE   delete single log
 * 
 */
