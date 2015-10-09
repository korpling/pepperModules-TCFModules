/**
 * Copyright 2009 Humboldt University of Berlin, INRIA.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */
package org.corpus_tools.peppermodules.tcfModules;

import org.corpus_tools.pepper.modules.PepperModuleProperties;
import org.corpus_tools.pepper.modules.PepperModuleProperty;


public class TCFImporterProperties extends PepperModuleProperties {
	/** property that sais if spans are always used in tagging of tokens or only if necessary. Value of String has to be "true" or "false". */
	public static final String PROP_SHRINK_TOKEN_ANNOTATIONS = "shrinkTokenAnnotations";
	/** if this property is "true" spans are reused by {@link TCFMapperImport} and all annotations are stored at a common span. In case "false" on each level tcf annotation level a new span is created. **/
	public static final String PROP_USE_COMMON_ANNOTATED_ELEMENT = "useCommonAnnotatedElement";
	
	public TCFImporterProperties(){
		addProperty(new PepperModuleProperty<Boolean>(PROP_SHRINK_TOKEN_ANNOTATIONS, Boolean.class, "property that sais if spans are always used in tagging of tokens or only if necessary. Value of String has to be \"true\" or \"false\".", true, true));
		addProperty(new PepperModuleProperty<Boolean>(PROP_USE_COMMON_ANNOTATED_ELEMENT, Boolean.class, "if this property is \"true\" spans are reused by TCFMapperImport and all annotations are stored at a common span. In case \"false\" on each level tcf annotation level a new span is created. Value of String has to be \"true\" or \"false\".", false, true));		
	}
	
	public boolean isShrinkTokenAnnotation(){
		boolean retVal = false;
		String prop = getProperty(PROP_SHRINK_TOKEN_ANNOTATIONS).getValue().toString();
		if((prop!=null)&&(!prop.isEmpty())){
			retVal = Boolean.valueOf(prop);
		}
		return retVal;
	}
	
	public boolean isUseCommonAnnotatedElement(){
		boolean retVal = false;
		String prop = getProperty(PROP_USE_COMMON_ANNOTATED_ELEMENT).getValue().toString();
		if((prop!=null)&&(!prop.isEmpty())){
			retVal = Boolean.valueOf(prop);
		}
		return retVal;
	}
}
