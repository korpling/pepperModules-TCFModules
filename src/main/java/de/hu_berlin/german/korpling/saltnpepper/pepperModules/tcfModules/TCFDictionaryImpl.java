package de.hu_berlin.german.korpling.saltnpepper.pepperModules.tcfModules;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

/**
* This class parses an xml file following the model of 'Bergleute_WebLicht_BitParReader'.
*
* @author XMLTagExtractor
**/
public class TCFDictionaryImpl extends DefaultHandler2 implements TCFDictionary {
		@Override
		public void startElement(	String uri,
				String localName,
				String qName,
				Attributes attributes)throws SAXException
		{
			if (TAG_TC_TOKENS.equals(qName)){
			}
			else if (TAG_PID.equals(qName)){
			}
			else if (TAG_RESOURCERELATIONLIST.equals(qName)){
			}
			else if (TAG_JOURNALFILEPROXYLIST.equals(qName)){
			}
			else if (TAG_WL_D_SPIN.equals(qName)){
			}
			else if (TAG_RESOURCENAME.equals(qName)){
			}
			else if (TAG_DESCRIPTIONS.equals(qName)){
			}
			else if (TAG_TC_PARSE.equals(qName)){
			}
			else if (TAG_TC_PARSING.equals(qName)){
			}
			else if (TAG_GENERALINFO.equals(qName)){
			}
			else if (TAG_CMD.equals(qName)){
			}
			else if (TAG_RESOURCECLASS.equals(qName)){
			}
			else if (TAG_HEADER.equals(qName)){
			}
			else if (TAG_TC_CONSTITUENT.equals(qName)){
			}
			else if (TAG_TC_SENTENCES.equals(qName)){
			}
			else if (TAG_PARAMETER.equals(qName)){
			}
			else if (TAG_MD_METADATA.equals(qName)){
			}
			else if (TAG_DESCRIPTION.equals(qName)){
			}
			else if (TAG_TC_TEXTCORPUS.equals(qName)){
			}
			else if (TAG_TC_LEMMA.equals(qName)){
			}
			else if (TAG_TOOLINCHAIN.equals(qName)){
			}
			else if (TAG_TC_TEXT.equals(qName)){
			}
			else if (TAG_RESOURCEPROXYLIST.equals(qName)){
			}
			else if (TAG_TC_TOKEN.equals(qName)){
			}
			else if (TAG_COMPONENTS.equals(qName)){
			}
			else if (TAG_WEBSERVICETOOLCHAIN.equals(qName)){
			}
			else if (TAG_TC_LEMMAS.equals(qName)){
			}
			else if (TAG_TC_SENTENCE.equals(qName)){
			}
			else if (TAG_MD_SERVICES.equals(qName)){
			}
			else if (TAG_TOOLCHAIN.equals(qName)){
			}
			else if (TAG_TC_TAG.equals(qName)){
			}
			else if (TAG_TC_POSTAGS.equals(qName)){
			}
			else if (TAG_RESOURCES.equals(qName)){
			}
		}
}
