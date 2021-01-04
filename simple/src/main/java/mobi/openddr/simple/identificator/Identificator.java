/*
 * Copyright (c) 2011-2021 OpenDDR LLC and others. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package mobi.openddr.simple.identificator;

import mobi.openddr.simple.model.BuiltObject;
import mobi.openddr.simple.model.UserAgent;
import org.w3c.ddr.simple.Evidence;

public interface Identificator {

    public BuiltObject get(String userAgent, int confidenceTreshold);

    public BuiltObject get(Evidence evdnc, int threshold);

    public BuiltObject get(UserAgent userAgent, int confidenceTreshold);

    public void completeInit();
}
