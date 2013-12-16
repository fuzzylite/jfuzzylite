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
package com.fuzzylite.factory;

import com.fuzzylite.hedge.Any;
import com.fuzzylite.hedge.Extremely;
import com.fuzzylite.hedge.Hedge;
import com.fuzzylite.hedge.Not;
import com.fuzzylite.hedge.Seldom;
import com.fuzzylite.hedge.Somewhat;
import com.fuzzylite.hedge.Very;

/**
 *
 * @author jcrada
 */
public class HedgeFactory extends Factory<Hedge> {

    public HedgeFactory() {
        register(new Any().getName(), Any.class);
        register(new Extremely().getName(), Extremely.class);
        register(new Not().getName(), Not.class);
        register(new Seldom().getName(), Seldom.class);
        register(new Somewhat().getName(), Somewhat.class);
        register(new Very().getName(), Very.class);
    }
}
