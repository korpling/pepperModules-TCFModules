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
package de.hu_berlin.german.korpling.saltnpepper.pepperModules.tcfModules;

import org.corpus_tools.pepper.modules.PepperModuleProperties;
import org.corpus_tools.pepper.modules.PepperModuleProperty;

public class TCFExporterProperties extends PepperModuleProperties{
	/** this property says which annotation key is used for spans over tokens that mark a line of text */
	public static final String PROP_TEXTSTRUCTURE_LINE_QNAME = "textstructure.line.qname";
	/** this property says which annotation value is used for spans over tokens that mark a line of text */
	public static final String PROP_TEXTSTRUCTURE_LINE_VALUE = "textstructure.line.value";
	/** this property says which annotation key is used for spans over tokens that mark a page */
	public static final String PROP_TEXTSTRUCTURE_PAGE_QNAME = "textstructure.page.qname";
	/** this property says which annotation value is used for spans over tokens that mark a line */
	public static final String PROP_TEXTSTRUCTURE_PAGE_VALUE = "textstructure.page.value";
	/** this property says whether empty tokens are allowed or not */
	public static final String PROP_EMPTY_TOKENS_ALLOWED = "allow.emptyTokens";
	/** this property says which annotation key is used for spans over tokens that mark a sentence */
	public static final String PROP_SENTENCE_QNAME = "sentence.qname";
	/** this property says which annotation value is used for spans over tokens that mark a sentence */
	public static final String PROP_SENTENCE_VALUE = "sentence.value";
	/** this property says which annotation key is used for POS annotations */
	public static final String PROP_POS_QNAME = "pos.qname";
	/** this property says which annotation annotation key is used for lemma annotations */
	public static final String PROP_LEMMA_QNAME = "lemma.qname";
	/** property description */
	public static final String PROP_NE_QNAME = "";
	
	
	public TCFExporterProperties(){
		addProperty(new PepperModuleProperty<String>(PROP_TEXTSTRUCTURE_LINE_QNAME, String.class, "This property says which annotation key is used for spans over tokens that mark a line of text.", "textstructure", false));
		addProperty(new PepperModuleProperty<String>(PROP_TEXTSTRUCTURE_LINE_VALUE, String.class, "This property says which annotation value is used for spans over tokens that mark a line of text.", "line", false));
		addProperty(new PepperModuleProperty<String>(PROP_TEXTSTRUCTURE_PAGE_QNAME, String.class, "This property says which annotation key is used for spans over tokens that mark a page of text.", "textstructure", false));
		addProperty(new PepperModuleProperty<String>(PROP_TEXTSTRUCTURE_PAGE_VALUE, String.class, "This property says which annotation value is used for spans over tokens that mark a page of text.", "page", false));
		addProperty(new PepperModuleProperty<Boolean>(PROP_EMPTY_TOKENS_ALLOWED, Boolean.class, "this property says whether empty tokens are allowed or not", true, false));
		addProperty(new PepperModuleProperty<String>(PROP_SENTENCE_QNAME, String.class, "This property says which annotation key is used for spans over tokens that mark a sentence.", "sentence", false));
		addProperty(new PepperModuleProperty<String>(PROP_SENTENCE_VALUE, String.class, "This property says which annotation key is used for POS annotations.", "sentence", false));
		addProperty(new PepperModuleProperty<String>(PROP_POS_QNAME, String.class, "This property says which annotation key is used for POS annotations.", "POS", false));
		addProperty(new PepperModuleProperty<String>(PROP_LEMMA_QNAME, String.class, "This property says which annotation annotation key is used for lemma annotations.", "LEMMA", false));
	}
	
	public String getTextstructureLineName(){		
		return getProperty(PROP_TEXTSTRUCTURE_LINE_QNAME).getValue().toString();		
	}
	
	public String getTextstructureLineValue(){		
		return getProperty(PROP_TEXTSTRUCTURE_LINE_VALUE).getValue().toString();		
	}
	
	public String getTextstructurePageName(){		
		return getProperty(PROP_TEXTSTRUCTURE_PAGE_QNAME).getValue().toString();		
	}
	
	public String getTextstructurePageValue(){		
		return getProperty(PROP_TEXTSTRUCTURE_PAGE_VALUE).getValue().toString();		
	}
	
	public Boolean isEmptyTokensAllowed(){
		boolean retVal = false;
		String prop = getProperty(PROP_EMPTY_TOKENS_ALLOWED).getValue().toString();
		if((prop!=null)&&(!prop.isEmpty())){
			retVal = Boolean.valueOf(prop);
		}
		return retVal;
	}
	
	public String getSentenceQName(){
		return getProperty(PROP_SENTENCE_QNAME).getValue().toString();
	}
	
	public String getSentenceValue(){
		return getProperty(PROP_SENTENCE_VALUE).getValue().toString();
	}
	
	public String getPOSQName(){
		return getProperty(PROP_POS_QNAME).getValue().toString();
	}
	
	public String getLemmaQName(){
		return getProperty(PROP_LEMMA_QNAME).getValue().toString();
	}
}
