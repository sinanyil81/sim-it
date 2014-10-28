/*
 * Copyright (c) 2014, Ege University
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the
 *   distribution.
 * - Neither the name of the copyright holder nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * @author Kasım Sinan YILDIRIM (sinanyil81@gmail.com)
 *
 */
package simulations.shared.avt;

public class AdaptiveValueTracker {
	final private float INCREASE_FACTOR = 2.0f;
	final private float DECREASE_FACTOR = 3.0f;
	
	final static public int FEEDBACK_GREATER = 0;
	final static public int FEEDBACK_LOWER = 1;
	final static public int FEEDBACK_GOOD = 2;
	final static public int FEEDBACK_NONE = 3;
	 
	float lowerBound = 0.0f;
	float upperBound = 0.0f;
	float value = 0.0f;
	
	float delta, deltaMin, deltaMax;
	
	int lastFeedback = FEEDBACK_NONE;
	
	public int NODE = 0;
			
    public AdaptiveValueTracker(float lBound,float uBound,float val,float dMin, float dMax)
    {
		lowerBound = lBound;
		upperBound = uBound;
		value = val;
		
		deltaMin = dMin;
		deltaMax = dMax;
		delta = dMax;
    }
    
    public float getValue(){
    
    	return value;
    }
    
    public float getDelta(){
    	return delta;
    }
    
    void increaseDelta(){
    	delta = delta * INCREASE_FACTOR;    
    	
    	if(delta > deltaMax){
    		delta = deltaMax;
    	}	
    }
    
    void decreaseDelta(){
    	   	
    	delta = delta / DECREASE_FACTOR;
    	
    	if(delta < deltaMin){
    		delta = deltaMin;
    	}     
    }
  
    void updateDelta(int feedback){
    	if(lastFeedback == FEEDBACK_NONE)
    		return;
    	
    	if (lastFeedback == FEEDBACK_GOOD) {  		
			if (feedback == FEEDBACK_GOOD) {
				decreaseDelta();
			} else {
//				decreaseDelta();
//				increaseDelta();
			}
		}else if (lastFeedback != feedback) {
			decreaseDelta();
		}else{
			increaseDelta();
		}    	
    }
    
    float min(float a,float b){
    	if(a<b) 
    		return a;
    		
    	return b;
    }
    
    float max(float a,float b){
    	if(a>b) 
    		return a;
    		
    	return b;
    }
    
    public void adjustValue(int feedback)
    {    	
		updateDelta(feedback);

		if (feedback != FEEDBACK_GOOD) {
			value = min(upperBound,max(lowerBound,value + delta*(feedback == FEEDBACK_GREATER ? 1.0f : -1.0f)));
		}
		
		lastFeedback = feedback;
    }
}
