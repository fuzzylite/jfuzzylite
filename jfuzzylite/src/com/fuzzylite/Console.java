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


package com.fuzzylite;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author jcrada
 */
public class Console {
//Import from file, -i file.fis -if format -o file -of format -r 100
    
    public static String usage(){
        Map<String,String> options = new LinkedHashMap<>();
        options.put("-i", "Input file");
        options.put("-if", "Format of input file: fis, fcl");
        options.put("-of", "Format of output: fis, fcl, c++, java, out");
        options.put("-o", "Output file");
        options.put("-ro", "Resolution of format 'out'");
        options.put("-so", "Separator of format 'out'");
        
        StringBuilder result = new StringBuilder();
        result.append("usage: java -jar jfuzzylite.jar ");
        for (String option : options.keySet()){
            result.append(String.format("[%s] ", option));
        }
        
        return result.toString();
    }
    public static void main(String[] args) {
        System.out.println(usage());
    }
}
