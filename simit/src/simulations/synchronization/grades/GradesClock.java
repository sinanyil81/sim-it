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
package simulations.synchronization.grades;

import simit.hardware.Register32;

 public class GradesClock {
    private Register32 value = new Register32();

    public float rate = 0.0f;

    Register32 updateLocalTime = new Register32();  

    public void update(Register32 local) {
            int timePassed = local.subtract(updateLocalTime).toInteger();
            timePassed += (int) (((float) timePassed) * rate);

            value = value.add(timePassed);
            this.updateLocalTime = new Register32(local);
    }

    public Register32 getValue(Register32 local) {
            int timePassed = local.subtract(updateLocalTime).toInteger();
            timePassed += (int) (((float) timePassed) * rate);

            return value.add(new Register32(timePassed));
    }

    public void setValue(Register32 time, Register32 local) {
            value = new Register32(time);
            this.updateLocalTime = new Register32(local);
    }
}
