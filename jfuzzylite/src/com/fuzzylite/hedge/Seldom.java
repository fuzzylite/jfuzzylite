/*   Copyright 2013 Juan Rada-Vilela

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package com.fuzzylite.hedge;

import com.fuzzylite.Op;

/**
 *
 * @author jcrada
 */
public class Seldom extends Hedge {

    @Override
    public double hedge(double x) {
        return Op.isLE(x, 0.5)
                ? Math.sqrt(x / 2.0)
                : 1.0 - Math.sqrt((1.0 - x) / 2.0);
    }
}
