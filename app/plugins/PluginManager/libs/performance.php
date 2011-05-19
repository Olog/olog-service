<?php

class Performance
{
    var $_timers = array();
    var $time_start;
    
    /**
    * Returns a singleton instance of the Performance class
    *
    * @return unknown
    */
    function &getInstance() 
    {
        static $instance = array();
        if (!$instance) {
            $instance[0] = &new Performance;
        }
            return $instance[0];
    }    
    
    /**
    * In the constructor we check if we already know the time our Application started (by checking for
    * the variable $TIME_START like cakephp set's it. Or we use the current time as our starttime.
    *
    * @return Performance
    */
    function Performance()
    {
        global $TIME_START;
    
        if (!empty($TIME_START))
            $this->time_start = $TIME_START;
        else 
            $this->time_start = $this->getMicrotime();
    }
    
    /**
    * Starts a new timer $key. If such a timer has already been started it's going to be reset to 0.
    *
    * @param string $key
    */
    function startTimer($key)
    {
        $_this =& Performance::getInstance();
    
        $key = strtolower($key);	   
        $_this->_timers[$key] = $_this->getMicrotime();
    }
    
    /**
    * Pauses the timer $key. You can resume it using Performance::resumeTimer()
    *
    * @param string $key
    */
    function pauseTimer($key)
    {
        $_this =& Performance::getInstance();
        
        $key = strtolower($key);
        $_this->_timers[$key] = array($_this->getTimer($key));
    }	
    
    /**
    * Resumes the timer $key if it had been paused before. If not nothing happens, and if such a timer
    * doesn't exists it get's created via Performance::startTimer() automatically.
    *
    * @param string $key
    */
    function resumeTimer($key)
    {
        $now = Performance::getMicrotime();
        $_this =& Performance::getInstance();
        
        $key = strtolower($key);
        
        if (!$_this->isKeySet($key))
            return $_this->startTimer($key);
        
        $timerStart = $_this->_timers[$key];	   
        
        if (is_array($timerStart))
            $_this->_timers[$key] = $now-array_pop($timerStart);
    }
    
    /**
    * Removes the timer $key from the list of timers
    *
    * @param unknown_type $key
    */
    function removeTimer($key)
    {
        $_this =& Performance::getInstance();
        
        $key = strtolower($key);	   
        if (array_key_exists($key, $_this->_timers) === true) 
            unset($_this->_timers[$key]);
    }	
    
    /**
    * Get's the current amount of time ellapsed for timer $timer.
    *
    * @param string $key
    * @return float
    */
    function getTimer($key, $now = null)
    {
        if (empty($now))
            $now = Performance::getMicrotime();
    
        $_this =& Performance::getInstance();
    
        $key = strtolower($key);    
        $timerStart = $_this->_timers[$key];
    
    
        if (is_array($timerStart))
            return array_pop($timerStart);
        else 
            return $now - $timerStart;
    }
    
    /**
    * Get's a list of all registered timers and their current amount of ellapsed time.
    *
    * @return array
    */
    function getTimers()
    {
        $now = Performance::getMicrotime();
    
        $_this =& Performance::getInstance();	   
    
        $timers = array();
    
        foreach ($_this->_timers as $key => $timer)
        {
            $timers[$key] = $_this->getTimer($key, $now);
        }
    
        $timers['_total'] = $now-$_this->time_start;
    
        return $timers;
    }
    
    /**
    * Returns the percantage that the timer $key has taken up in time compared
    * to the total execution time of the script (see the constructor to make sure
    * this works).
    *
    * @param string $key
    * @param float $now
    * @param float $timer
    * @return float
    */
    function getTimerPercantage($key, $now = null, $timer = null)
    {
        if (empty($now))
            $now = Performance::getMicrotime();
    
        $_this =& Performance::getInstance();
        if (empty($timer))
            $timer = $_this->getTimer($key, $now);
        
        return $timer.' ('.round((($timer)/($now-$this->time_start)*100), 2).'%)';
    }
    
    /**
    * Get's a list of all timers together with the time percantage they have used up.
    * The total may add up to over 100% if some of the timers have been running at the
    * same time.
    *
    * @return array
    */
    function getTimersPercantage()
    {
        $now = Performance::getMicrotime();
        $_this =& Performance::getInstance();
        
        $timers = $_this->getTimers();
        $percantageTimers = array();
        
        foreach ($timers as $key => $timer)
        {
            $percantageTimers[$key] = $_this->getTimerPercantage($key, $now, $timer);
        }       
        
        return $percantageTimers;	       
    }
    
    /**
    * Checks if the timer $key exists or not.
    *
    * @param unknown_type $key
    * @return unknown
    */
    function isKeySet($key) {
        $_this =& Performance::getInstance();
        
        $key = strtolower($key);
        return array_key_exists($key, $_this->_timers);
    }	
    
    /**
    * A convenience function for debug(Performance::getTimer($key));
    *
    * @param string $key
    */
    function debugTimer($key)
    {
        debug(Performance::getTimer($key));
    }
    
    /**
    * A convenience function for debug(Performance::getTimers($key));
    *
    * @param string $key
    */	
    function debugTimers()
    {
        debug(Performance::getTimers());
    }
    
    /**
    * A convenience function for debug(Performance::getTimerPercantage($key));
    *
    * @param string $key
    */		
    function debugTimerPercantage($key)
    {
        debug(Performance::getTimerPercantage($key));
    }
    
    /**
    * A convenience function for debug(Performance::getTimersPercantage($key));
    *
    * @param string $key
    */		
    function debugTimersPercantage()
    {
        debug(Performance::getTimersPercantage());
    }	
    
    /**
    * Returns the microtime in seconds as a float. I know php5 / cakephp already have this function,
    * but I wanted a maximum of reusability for this class.
    *
    * @return float
    */
    function getMicrotime()
    {
        return array_sum(explode(chr(32), microtime()));
    }	
}

?>