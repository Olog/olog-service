/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package edu.msu.nscl.olog;

/**
 * TODO
 * 1. The absence of the propertiesResource similar to logbooksResource and 
 * TagsResource.
 * 
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
 * POST     creating multiple properties
 * 
 * MISSSING
 * .../properties/<propertyName>
 * GET      retrieve one propertyName
 * PUT      create/replace once property
 * POST     update a property
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
