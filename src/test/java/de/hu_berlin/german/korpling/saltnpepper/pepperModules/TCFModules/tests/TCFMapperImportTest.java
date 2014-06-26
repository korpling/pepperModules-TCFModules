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
package de.hu_berlin.german.korpling.saltnpepper.pepperModules.TCFModules.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hu_berlin.german.korpling.saltnpepper.pepperModules.tcfModules.TCFDictionary;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.tcfModules.TCFImporterProperties;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.tcfModules.TCFMapperImport;
import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Node;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SPointingRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructure;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STYPE_NAME;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SLayer;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltSample.SaltSample;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltSemantics.SALT_SEMANTIC_NAMES;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltSemantics.SLemmaAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltSemantics.SPOSAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltSemantics.SaltSemanticsPackage;

public class TCFMapperImportTest {

	private TCFMapperImport fixture;
	private static final String EXAMPLE_TEXT = "Is this example more complicated than it appears to be?";
	private static final String EXAMPLE_TEXT_SHRINK = "I love New York.";
	private static final String EXAMPLE_TEXT_REFERENCE = "I love New York. It is the most beautiful place.";
	private static final String EXAMPLE_TEXT_NAMED_ENTITIES = "Martin loves New York. He lives in Friedrichshain.";
	private static final String EXAMPLE_TEXT_ORTHOGRAPHY = "Ei laaf Nuh Jork.";
	private static final String EXAMPLE_TEXT_GEO = "New York is not Berlin.";
	private static final String EXAMPLE_TEXT_DISCOURSE_CONNECTIVES = "Since I went there I know more than I knew before.";
	private static final String LOCATION_TEST_PRIMARY_TEXT = "/pepper-test/tcfImporterTestPrimaryData.xml";
	private static final String LOCATION_TEST_TOKENS = "/pepper-test/tcfImporterTestTokens.xml";
	private static final String LOCATION_TEST_TOKENS_POS = "/pepper-test/tcfImporterTestTokensPos.xml";
	private static final String LOCATION_TEST_TOKENS_LEMMA = "/pepper-test/tcfImporterTestTokensLemma.xml";
	private static final String LOCATION_TEST_SENTENCE = "/pepper-test/tcfImporterTestSentence.xml";
	private static final String LOCATION_TEST_DEPENDENCIES_NO_MULTIGOVS = "/pepper-test/tcfImporterTestDependenciesNoMultigovs.xml";
	private static final String LOCATION_TEST_CONSTITUENT_PARSING = "/pepper-test/tcfImporterTestConstituentParsing.xml";
	private static final String LOCATION_TEST_MORPHOLOGY = "/pepper-test/tcfImporterTestMorphology.xml";
	private static final String LOCATION_TEST_REFERENCES = "/pepper-test/tcfImporterTestReferences.xml";
	private static final String LOCATION_TEST_NE = "/pepper-test/tcfImporterTestNE.xml";
	private static final String LOCATION_TEST_PHONETICS = "/pepper-test/tcfImporterTestNE.xml";	
	private static final String LOCATION_TEST_GEO = "/pepper-test/tcfImporterTestGEO.xml";	
	private static final String LOCATION_TEST_ORTHOGRAPHY = "/pepper-test/tcfImporterTestOrthography.xml";
	private static final String LOCATION_TEST_TEXTSTRUCTURE = "/pepper-test/tcfImporterTestTextstructure.xml";
	private static final String LOCATION_TEST_LEXICALSEMANTICS = "/pepper-test/tcfImporterTestLexicalSemantics.xml";
	private static final String LOCATION_TEST_DISCOURSE_CONNECTIVES = "/pepper-test/tcfImporterTestDiscourseConnectives.xml";
	
	private static final boolean SPAN_REUSE = false;
	private static final Logger logger = LoggerFactory.getLogger(TCFMapperImportTest.class);
	private static final boolean DEBUG = false;	
	
	public TCFMapperImport getFixture() {
		return fixture;
	}

	public void setFixture(TCFMapperImport fixture) {
		this.fixture = fixture;
	}
		
	@Before
	public void setUp()
	{
		setFixture(new TCFMapperImport());
		getFixture().setProperties(new TCFImporterProperties());
	}
	
	/**
	 * This method tests if a valid TCF-XML-structure containing the primary text
	 * is converted to salt correctly by {@link TCFMapperImport} 
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testPrimaryText() throws XMLStreamException, FileNotFoundException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o= XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);
		xmlWriter.writeStartDocument();
			xmlWriter.writeProcessingInstruction(TCFDictionary.TCF_PI);
			xmlWriter.writeStartElement(TCFDictionary.NS_WL, TCFDictionary.TAG_WL_D_SPIN, TCFDictionary.NS_VALUE_WL);;
				xmlWriter.writeNamespace(TCFDictionary.NS_WL, TCFDictionary.NS_VALUE_WL);
				xmlWriter.writeNamespace(TCFDictionary.NS_ED, TCFDictionary.NS_VALUE_ED);
				xmlWriter.writeNamespace(TCFDictionary.NS_LX, TCFDictionary.NS_VALUE_LX);
				xmlWriter.writeNamespace(TCFDictionary.NS_MD, TCFDictionary.NS_VALUE_MD);
				xmlWriter.writeNamespace(TCFDictionary.NS_TC, TCFDictionary.NS_VALUE_TC);
				xmlWriter.writeAttribute(TCFDictionary.ATT_VERSION, "4.0");
				xmlWriter.writeStartElement(TCFDictionary.NS_MD, TCFDictionary.TAG_MD_METADATA, TCFDictionary.NS_VALUE_MD);
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTCORPUS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_LANG, "de");
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXT, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeCharacters(EXAMPLE_TEXT);
					xmlWriter.writeEndElement();
				xmlWriter.writeEndElement();
			xmlWriter.writeEndElement();
		xmlWriter.writeEndDocument();		
		
		/*generating salt sample*/
		SDocument doc = SaltFactory.eINSTANCE.createSDocument();
		doc.setSDocumentGraph(SaltFactory.eINSTANCE.createSDocumentGraph());
		STextualDS primaryText = SaltFactory.eINSTANCE.createSTextualDS();
		primaryText.setSText(EXAMPLE_TEXT);
		doc.getSDocumentGraph().addSNode(primaryText);
		
		
		/* setting variables*/
		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_PRIMARY_TEXT);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		this.getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, SPAN_REUSE);
		
		/* start mapper */
				
		this.getFixture().mapSDocument();
				
		
		/* compare template salt model to imported salt model */
		assertNotNull(getFixture().getSDocument());
		assertNotNull(getFixture().getSDocument().getSDocumentGraph());
		assertEquals(doc.getSDocumentGraph().getSTextualDSs().size(), getFixture().getSDocument().getSDocumentGraph().getSTextualDSs().size());		
		assertEquals(doc.getSDocumentGraph().getSTextualDSs().get(0).getSText().length(), getFixture().getSDocument().getSDocumentGraph().getSTextualDSs().get(0).getSText().length());
		assertEquals(doc.getSDocumentGraph().getSTextualDSs().get(0).getSText(), getFixture().getSDocument().getSDocumentGraph().getSTextualDSs().get(0).getSText());		
	}
	
	/**
	 * This method tests if a valid TCF-XML-structure containing the tokens
	 * is converted to salt correctly by {@link TCFMapperImport} 
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testTokens() throws XMLStreamException, FileNotFoundException{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o= XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);		
		
		xmlWriter.writeStartDocument();
			xmlWriter.writeProcessingInstruction(TCFDictionary.TCF_PI);
			xmlWriter.writeStartElement(TCFDictionary.NS_WL, TCFDictionary.TAG_WL_D_SPIN, TCFDictionary.NS_VALUE_WL);
			xmlWriter.writeNamespace(TCFDictionary.NS_WL, TCFDictionary.NS_VALUE_WL);
			xmlWriter.writeNamespace(TCFDictionary.NS_ED, TCFDictionary.NS_VALUE_ED);
			xmlWriter.writeNamespace(TCFDictionary.NS_LX, TCFDictionary.NS_VALUE_LX);
			xmlWriter.writeNamespace(TCFDictionary.NS_MD, TCFDictionary.NS_VALUE_MD);
			xmlWriter.writeNamespace(TCFDictionary.NS_TC, TCFDictionary.NS_VALUE_TC);
			xmlWriter.writeAttribute(TCFDictionary.ATT_VERSION, "4.0");
				xmlWriter.writeStartElement(TCFDictionary.NS_MD, TCFDictionary.TAG_MD_METADATA, TCFDictionary.NS_VALUE_MD);
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTCORPUS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXT, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeCharacters(EXAMPLE_TEXT);
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKENS, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t1");
							xmlWriter.writeCharacters("Is");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t2");
							xmlWriter.writeCharacters("this");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t3");
							xmlWriter.writeCharacters("example");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t4");
							xmlWriter.writeCharacters("more");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t5");
							xmlWriter.writeCharacters("complicated");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t6");
							xmlWriter.writeCharacters("than");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t7");
							xmlWriter.writeCharacters("it");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t8");
							xmlWriter.writeCharacters("appears");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t9");
							xmlWriter.writeCharacters("to");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t10");
							xmlWriter.writeCharacters("be");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t11");
							xmlWriter.writeCharacters("?");
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();
				xmlWriter.writeEndElement();
			xmlWriter.writeEndElement();
		xmlWriter.writeEndDocument();
		
		/* generating salt sample */
		SDocument doc = SaltFactory.eINSTANCE.createSDocument();
		doc.setSDocumentGraph(SaltFactory.eINSTANCE.createSDocumentGraph());
		SaltSample.createPrimaryData(doc);
		SaltSample.createTokens2(doc);
		SDocumentGraph docGraph = doc.getSDocumentGraph();
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_TOKENS);
		tmpOut.getParentFile().mkdirs();//necessary? – is this test meant to be totally independent from the other one?
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		this.getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, SPAN_REUSE);
		
		/* start mapper */
				
		this.getFixture().mapSDocument();
				
		
		/* test from testPrimaryData*/
		SDocumentGraph fixGraph = getFixture().getSDocument().getSDocumentGraph();
		
		assertNotNull(getFixture().getSDocument());
		assertNotNull(getFixture().getSDocument().getSDocumentGraph());
		assertEquals(docGraph.getSTextualDSs().size(), fixGraph.getSTextualDSs().size());		
		assertEquals(docGraph.getSTextualDSs().get(0).getSText().length(), fixGraph.getSTextualDSs().get(0).getSText().length());
		assertEquals(docGraph.getSTextualDSs().get(0).getSText(), fixGraph.getSTextualDSs().get(0).getSText());	
		
		/* compare template salt model to imported salt model */
		
		EList<SToken> docTokens = docGraph.getSTokens();
		EList<SToken> fixTokens = fixGraph.getSTokens();
		
		assertNotEquals(fixGraph.getSTextualDSs().size(), 0);
		assertNotEquals(fixTokens.size(), 0);
		assertEquals(docTokens.size(), fixTokens.size());		
		
		
		for(int i=0; i<docTokens.size(); i++){
			assertEquals(docGraph.getSText(docTokens.get(i)), fixGraph.getSText(fixTokens.get(i)));
			if(DEBUG){}
		}
		
	}

	/**
	 * This method tests if a valid TCF-XML-structure containing pos-tagged tokens
	 * is converted to salt correctly by {@link TCFMapperImport}. In this test case
	 * a single annotation over a sequence of tokens is used. {@link TCFMapperImport} is
	 * supposed to build a {@link SSpan} over a token sequence that is POS annotated
	 * while single tokens annotations are directly stored at the {@link SToken} object as
	 * {@link SPOSAnnotation}.
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testTokensPosShrinked() throws XMLStreamException, FileNotFoundException{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o= XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);		
		
		xmlWriter.writeStartDocument();
			xmlWriter.writeProcessingInstruction(TCFDictionary.TCF_PI);
			xmlWriter.writeStartElement(TCFDictionary.NS_WL, TCFDictionary.TAG_WL_D_SPIN, TCFDictionary.NS_VALUE_WL);
			xmlWriter.writeNamespace(TCFDictionary.NS_WL, TCFDictionary.NS_VALUE_WL);
			xmlWriter.writeNamespace(TCFDictionary.NS_ED, TCFDictionary.NS_VALUE_ED);
			xmlWriter.writeNamespace(TCFDictionary.NS_LX, TCFDictionary.NS_VALUE_LX);
			xmlWriter.writeNamespace(TCFDictionary.NS_MD, TCFDictionary.NS_VALUE_MD);
			xmlWriter.writeNamespace(TCFDictionary.NS_TC, TCFDictionary.NS_VALUE_TC);
			xmlWriter.writeAttribute(TCFDictionary.ATT_VERSION, "4.0");
				xmlWriter.writeStartElement(TCFDictionary.NS_MD, TCFDictionary.TAG_MD_METADATA, TCFDictionary.NS_VALUE_MD);
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTCORPUS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXT, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeCharacters(EXAMPLE_TEXT_SHRINK);
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKENS, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t1");
							xmlWriter.writeCharacters("I");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t2");
							xmlWriter.writeCharacters("love");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t3");
							xmlWriter.writeCharacters("New");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t4");
							xmlWriter.writeCharacters("York");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t5");
							xmlWriter.writeCharacters(".");
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.TAG_TC_POSTAGS);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TAGSET, "penn treebank");
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_TAG);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt1");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t1");
						xmlWriter.writeCharacters("PP");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_TAG);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt2");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t2");
						xmlWriter.writeCharacters("VBZ");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_TAG);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt3");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t3 t4");
						xmlWriter.writeCharacters("NNP");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_TAG);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt4");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t5");
						xmlWriter.writeCharacters(".");
						xmlWriter.writeEndElement();						
					xmlWriter.writeEndElement();
				xmlWriter.writeEndElement();
			xmlWriter.writeEndElement();
		xmlWriter.writeEndDocument();
				
		/* generating salt sample */
		SDocument doc = SaltFactory.eINSTANCE.createSDocument();
		doc.setSDocumentGraph(SaltFactory.eINSTANCE.createSDocumentGraph());
		SDocumentGraph docGraph = doc.getSDocumentGraph();
		docGraph.createSTextualDS("I love New York.");
		docGraph.tokenize();
		
		SLayer posLayer = SaltFactory.eINSTANCE.createSLayer();
		posLayer.setSName(TCFMapperImport.LAYER_POS);
		
		SAnnotation anno = SaltFactory.eINSTANCE.createSPOSAnnotation();
		anno.setValue("PP");
		docGraph.getSTokens().get(0).addSAnnotation(anno);
		posLayer.getSNodes().add(docGraph.getSTokens().get(0));

		anno = SaltFactory.eINSTANCE.createSPOSAnnotation();
		anno.setValue("VBZ");
		docGraph.getSTokens().get(1).addSAnnotation(anno);
		posLayer.getSNodes().add(docGraph.getSTokens().get(1));
		
		anno = SaltFactory.eINSTANCE.createSPOSAnnotation();
		anno.setValue("NNP");
		SSpan newYork = docGraph.createSSpan(docGraph.getSTokens().get(2));
		docGraph.addSNode(newYork, docGraph.getSTokens().get(3), STYPE_NAME.SSPANNING_RELATION);
		newYork.addSAnnotation(anno);
		posLayer.getSNodes().add(newYork);
		
		anno = SaltFactory.eINSTANCE.createSPOSAnnotation();
		anno.setValue(".");
		docGraph.getSTokens().get(4).addSAnnotation(anno);
		posLayer.getSNodes().add(docGraph.getSTokens().get(4));
		
		docGraph.addSLayer(posLayer);
		
		/**
		 * TODO SLayer <-- maybe put tagset information in there
		 */
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_TOKENS_POS);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		this.getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, true);
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, SPAN_REUSE);
		
		/* start mapper */
				
		this.getFixture().mapSDocument();
				
		
		/* compare template salt model to imported salt model */
		
		SDocumentGraph fixGraph = this.getFixture().getSDocument().getSDocumentGraph();
		String posQName = SaltSemanticsPackage.eNS_PREFIX+"::"+SALT_SEMANTIC_NAMES.POS.toString();
		SLayer fixLayer = fixGraph.getSLayerByName(TCFMapperImport.LAYER_POS).get(0); 
		
		assertNotNull(fixLayer);
		assertEquals(posLayer.getSNodes().size(), fixLayer.getSNodes().size());
		assertEquals(docGraph.getSTokens().size(), fixGraph.getSTokens().size());		
		for(int i=0; i<docGraph.getSNodes().size(); i++){
			assertEquals(docGraph.getSNodes().get(i).getSAnnotation(posQName), fixGraph.getSNodes().get(i).getSAnnotation(posQName));
			assertEquals(docGraph.getSNodes().get(i).getClass().toString(), fixGraph.getSNodes().get(i).getClass().toString());
			assertEquals(docGraph.getSText(docGraph.getSNodes().get(i)), fixGraph.getSText(fixGraph.getSNodes().get(i)));
		}
	}
	
	/**
	 * This method tests if a valid TCF-XML-structure containing pos-tagged tokens
	 * is converted to salt correctly by {@link TCFMapperImport}. In this test case
	 * a single annotation over a sequence of tokens is used. {@link TCFMapperImport} is
	 * supposed to build a {@link SSpan} both over a token sequence and a single token.
	 * The {@link SPOSAnnotation} has to be added to the {@link SSpan} object.
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException
	 */
	@Test
	public void testTokensPosNotShrinked() throws XMLStreamException, FileNotFoundException{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o= XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);		
		
		xmlWriter.writeStartDocument();
			xmlWriter.writeProcessingInstruction(TCFDictionary.TCF_PI);
			xmlWriter.writeStartElement(TCFDictionary.NS_WL, TCFDictionary.TAG_WL_D_SPIN, TCFDictionary.NS_VALUE_WL);
			xmlWriter.writeNamespace(TCFDictionary.NS_WL, TCFDictionary.NS_VALUE_WL);
			xmlWriter.writeNamespace(TCFDictionary.NS_ED, TCFDictionary.NS_VALUE_ED);
			xmlWriter.writeNamespace(TCFDictionary.NS_LX, TCFDictionary.NS_VALUE_LX);
			xmlWriter.writeNamespace(TCFDictionary.NS_MD, TCFDictionary.NS_VALUE_MD);
			xmlWriter.writeNamespace(TCFDictionary.NS_TC, TCFDictionary.NS_VALUE_TC);
			xmlWriter.writeAttribute(TCFDictionary.ATT_VERSION, "4.0");
				xmlWriter.writeStartElement(TCFDictionary.NS_MD, TCFDictionary.TAG_MD_METADATA, TCFDictionary.NS_VALUE_MD);
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTCORPUS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXT, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeCharacters(EXAMPLE_TEXT_SHRINK);
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKENS, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t1");
							xmlWriter.writeCharacters("I");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t2");
							xmlWriter.writeCharacters("love");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t3");
							xmlWriter.writeCharacters("New");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t4");
							xmlWriter.writeCharacters("York");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t5");
							xmlWriter.writeCharacters(".");
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.TAG_TC_POSTAGS);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TAGSET, "penn treebank");
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_TAG);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt1");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t1");
						xmlWriter.writeCharacters("PP");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_TAG);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt2");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t2");
						xmlWriter.writeCharacters("VBZ");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_TAG);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt3");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t3 t4");
						xmlWriter.writeCharacters("NNP");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_TAG);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt4");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t5");
						xmlWriter.writeCharacters(".");
						xmlWriter.writeEndElement();						
					xmlWriter.writeEndElement();
				xmlWriter.writeEndElement();
			xmlWriter.writeEndElement();
		xmlWriter.writeEndDocument();
				
		/* generating salt sample */
		SDocument doc = SaltFactory.eINSTANCE.createSDocument();
		doc.setSDocumentGraph(SaltFactory.eINSTANCE.createSDocumentGraph());
		SDocumentGraph docGraph = doc.getSDocumentGraph();
		docGraph.createSTextualDS("I love New York.");
		docGraph.tokenize();
		
		SLayer posLayer = SaltFactory.eINSTANCE.createSLayer();
		posLayer.setSName(TCFMapperImport.LAYER_POS);
		
		SAnnotation anno = SaltFactory.eINSTANCE.createSPOSAnnotation();
		SSpan sSpan = docGraph.createSSpan(docGraph.getSTokens().get(0));
		anno.setValue("PP");
		sSpan.addSAnnotation(anno);
		posLayer.getSNodes().add(sSpan);

		anno = SaltFactory.eINSTANCE.createSPOSAnnotation();
		sSpan = docGraph.createSSpan(docGraph.getSTokens().get(1));
		anno.setValue("VBZ");
		sSpan.addSAnnotation(anno);
		posLayer.getSNodes().add(sSpan);
		
		anno = SaltFactory.eINSTANCE.createSPOSAnnotation();
		anno.setValue("NNP");
		sSpan = docGraph.createSSpan(docGraph.getSTokens().get(2));
		docGraph.addSNode(sSpan, docGraph.getSTokens().get(3), STYPE_NAME.SSPANNING_RELATION);
		sSpan.addSAnnotation(anno);
		posLayer.getSNodes().add(sSpan);
		
		anno = SaltFactory.eINSTANCE.createSPOSAnnotation();
		sSpan = docGraph.createSSpan(docGraph.getSTokens().get(4));
		anno.setValue(".");
		sSpan.addSAnnotation(anno);
		posLayer.getSNodes().add(sSpan);		
		
		/*TODO tagset information*/
		docGraph.addSLayer(posLayer);
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_TOKENS_POS);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		this.getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, false);
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, SPAN_REUSE);
						
		
		/* start mapper */
				
		this.getFixture().mapSDocument();
		
		/* compare template salt model to imported salt model */
		
		SDocumentGraph fixGraph = this.getFixture().getSDocument().getSDocumentGraph();
		SLayer fixLayer = fixGraph.getSLayerByName(TCFMapperImport.LAYER_POS).get(0);
		String posQName = SaltSemanticsPackage.eNS_PREFIX+"::"+SALT_SEMANTIC_NAMES.POS.toString();		
		
		assertEquals(docGraph.getSSpans().size(), fixGraph.getSSpans().size());
		assertEquals(fixGraph.getSSpans().size(), docGraph.getSTokens().size()-1);
		assertNotNull(fixLayer);
		assertEquals(posLayer.getSNodes().size(), fixLayer.getSNodes().size());
		for(int i=0; i<docGraph.getSSpans().size(); i++){			
			assertNotNull(fixGraph.getSSpans().get(i).getSAnnotation(posQName));
			assertEquals(docGraph.getSSpans().get(i).getSAnnotation(posQName).getValue(), fixGraph.getSSpans().get(i).getSAnnotation(posQName).getValue());
			assertEquals(docGraph.getSText(docGraph.getSSpans().get(i)), fixGraph.getSText(fixGraph.getSSpans().get(i)));
		}
	}
	
	/**
	 * This method tests if a valid TCF-XML-structure containing a sentence
	 * and tokens is converted to salt correctly by {@link TCFMapperImport}  
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testSentence() throws XMLStreamException, FileNotFoundException{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o= XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);	

		xmlWriter.writeStartDocument();
		xmlWriter.writeProcessingInstruction(TCFDictionary.TCF_PI);
		xmlWriter.writeStartElement(TCFDictionary.NS_WL, TCFDictionary.TAG_WL_D_SPIN, TCFDictionary.NS_VALUE_WL);
		xmlWriter.writeNamespace(TCFDictionary.NS_WL, TCFDictionary.NS_VALUE_WL);
		xmlWriter.writeNamespace(TCFDictionary.NS_ED, TCFDictionary.NS_VALUE_ED);
		xmlWriter.writeNamespace(TCFDictionary.NS_LX, TCFDictionary.NS_VALUE_LX);
		xmlWriter.writeNamespace(TCFDictionary.NS_MD, TCFDictionary.NS_VALUE_MD);
		xmlWriter.writeNamespace(TCFDictionary.NS_TC, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_VERSION, "4.0");
		xmlWriter.writeStartElement(TCFDictionary.NS_MD, TCFDictionary.TAG_MD_METADATA, TCFDictionary.NS_VALUE_MD);
		xmlWriter.writeEndElement();
		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTCORPUS, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXT, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeCharacters(EXAMPLE_TEXT);
		xmlWriter.writeEndElement();
		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKENS, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t1");
		xmlWriter.writeCharacters("Is");
		xmlWriter.writeEndElement();
		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t2");
		xmlWriter.writeCharacters("this");
		xmlWriter.writeEndElement();
		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t3");
		xmlWriter.writeCharacters("example");
		xmlWriter.writeEndElement();
		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t4");
		xmlWriter.writeCharacters("more");
		xmlWriter.writeEndElement();
		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t5");
		xmlWriter.writeCharacters("complicated");
		xmlWriter.writeEndElement();
		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t6");
		xmlWriter.writeCharacters("than");
		xmlWriter.writeEndElement();
		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t7");
		xmlWriter.writeCharacters("it");
		xmlWriter.writeEndElement();
		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t8");
		xmlWriter.writeCharacters("appears");
		xmlWriter.writeEndElement();
		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t9");
		xmlWriter.writeCharacters("to");
		xmlWriter.writeEndElement();
		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t10");
		xmlWriter.writeCharacters("be");
		xmlWriter.writeEndElement();
		xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t11");
		xmlWriter.writeCharacters("?");
		xmlWriter.writeEndElement();
		xmlWriter.writeEndElement();
		xmlWriter.writeStartElement(TCFDictionary.TAG_TC_SENTENCES);
		xmlWriter.writeStartElement(TCFDictionary.TAG_TC_SENTENCE);
		xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "s_0");
		xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t1 t2 t3 t4 t5 t6 t7 t8 t9 t10 t11");
		xmlWriter.writeEndElement();
		xmlWriter.writeEndElement();
		xmlWriter.writeEndElement();
		xmlWriter.writeEndElement();
		xmlWriter.writeEndDocument();

		/* generating salt sample */
		SDocument doc = SaltFactory.eINSTANCE.createSDocument();
		doc.setSDocumentGraph(SaltFactory.eINSTANCE.createSDocumentGraph());
		SaltSample.createPrimaryData(doc);
		SaltSample.createTokens2(doc);	
		SDocumentGraph docGraph = doc.getSDocumentGraph();

		/* adding sentence span */
		SSpan docSentence = docGraph.createSSpan(docGraph.getSTokens());
		docSentence.createSAnnotation(null, TCFMapperImport.LEVEL_SENTENCE, "s_0");

		/* setting variables */	
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_SENTENCE);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);	
		p.println(outStream.toString());
		p.close();
		this.getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, SPAN_REUSE);
		
		/* start mapper */
				
		this.getFixture().mapSDocument();
				
		
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = getFixture().getSDocument().getSDocumentGraph();

		assertNotEquals(fixGraph.getSSpans().size(), 0);
		assertEquals(fixGraph.getSSpans().size(), docGraph.getSSpans().size());

		SSpan fixSpan = fixGraph.getSSpans().get(0);
		SSpan docSpan = docGraph.getSSpans().get(0);

		EList<STYPE_NAME> typeList = new BasicEList<STYPE_NAME>();
		typeList.add(STYPE_NAME.SSPANNING_RELATION);


		EList<SToken> docSpanTokens = docGraph.getOverlappedSTokens(docSpan, typeList);
		EList<SToken> fixSpanTokens = fixGraph.getOverlappedSTokens(fixSpan, typeList);
		for(int i=0; i<docSpanTokens.size(); i++){
			assertEquals(docGraph.getSText(docSpanTokens.get(i)), fixGraph.getSText(fixSpanTokens.get(i)));
		}
	}
		
	/**
	 * This method tests if a valid TCF-XML-structure containing lemmas
	 * and tokens is converted to salt correctly by {@link TCFMapperImport}.
	 * The mapper is supposed to map annotations as {@link SLemmaAnnotation} objects
	 * directly on the {@link SToken} objects for single token annotations and on
	 * an {@link SSpan} object in case of multiple token annotation.  
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testTokensLemmaShrinked() throws XMLStreamException, FileNotFoundException{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o= XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);		
		
		xmlWriter.writeStartDocument();
			xmlWriter.writeProcessingInstruction(TCFDictionary.TCF_PI);
			xmlWriter.writeStartElement(TCFDictionary.NS_WL, TCFDictionary.TAG_WL_D_SPIN, TCFDictionary.NS_VALUE_WL);
			xmlWriter.writeNamespace(TCFDictionary.NS_WL, TCFDictionary.NS_VALUE_WL);
			xmlWriter.writeNamespace(TCFDictionary.NS_ED, TCFDictionary.NS_VALUE_ED);
			xmlWriter.writeNamespace(TCFDictionary.NS_LX, TCFDictionary.NS_VALUE_LX);
			xmlWriter.writeNamespace(TCFDictionary.NS_MD, TCFDictionary.NS_VALUE_MD);
			xmlWriter.writeNamespace(TCFDictionary.NS_TC, TCFDictionary.NS_VALUE_TC);
			xmlWriter.writeAttribute(TCFDictionary.ATT_VERSION, "4.0");
				xmlWriter.writeStartElement(TCFDictionary.NS_MD, TCFDictionary.TAG_MD_METADATA, TCFDictionary.NS_VALUE_MD);
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTCORPUS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXT, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeCharacters(EXAMPLE_TEXT_SHRINK);
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKENS, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t1");
							xmlWriter.writeCharacters("I");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t2");
							xmlWriter.writeCharacters("love");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t3");
							xmlWriter.writeCharacters("New");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t4");
							xmlWriter.writeCharacters("York");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t5");
							xmlWriter.writeCharacters(".");
						xmlWriter.writeEndElement();						
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_LEMMAS, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_LEMMA, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt1");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t1");
						xmlWriter.writeCharacters("I");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_LEMMA, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt2");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t2");
						xmlWriter.writeCharacters("love");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_LEMMA, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt3");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t3 t4");
						xmlWriter.writeCharacters("New York");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_LEMMA, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt4");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t5");
						xmlWriter.writeCharacters(".");
						xmlWriter.writeEndElement();						
					xmlWriter.writeEndElement();
				xmlWriter.writeEndElement();
			xmlWriter.writeEndElement();
		xmlWriter.writeEndDocument();
		
		/* generating salt sample */
		SDocument doc = SaltFactory.eINSTANCE.createSDocument();
		doc.setSDocumentGraph(SaltFactory.eINSTANCE.createSDocumentGraph());		
		SDocumentGraph docGraph = doc.getSDocumentGraph();
		docGraph.createSTextualDS(EXAMPLE_TEXT_SHRINK);
		docGraph.tokenize();
		/*TODO add lemma layer in salt sample*/
		SLayer docLemmaLayer = SaltFactory.eINSTANCE.createSLayer();
		docLemmaLayer.setSName(TCFMapperImport.LAYER_LEMMA);
		EList<SNode> docLemma = docLemmaLayer.getSNodes();
		EList<SToken> docTokens = docGraph.getSTokens();
		
		SNode sNode = docTokens.get(0);
		SAnnotation sAnno = SaltFactory.eINSTANCE.createSLemmaAnnotation();
		sAnno.setValue("I");
		sNode.addSAnnotation(sAnno);
		docLemma.add(sNode);
		
		sNode = docTokens.get(1);
		sAnno = SaltFactory.eINSTANCE.createSLemmaAnnotation();
		sAnno.setValue("love");
		sNode.addSAnnotation(sAnno);
		docLemma.add(sNode);
		
		sNode = docGraph.createSSpan(docTokens.get(2));
		docGraph.addSNode(sNode, docTokens.get(3), STYPE_NAME.SSPANNING_RELATION);
		sAnno = SaltFactory.eINSTANCE.createSLemmaAnnotation();
		sAnno.setValue("New York");
		sNode.addSAnnotation(sAnno);
		docLemma.add(sNode);
		
		sNode = docTokens.get(4);
		sAnno = SaltFactory.eINSTANCE.createSLemmaAnnotation();
		sAnno.setValue(".");
		sNode.addSAnnotation(sAnno);
		docLemma.add(sNode);		
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_TOKENS_LEMMA);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		this.getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, true);
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, SPAN_REUSE);
		
		/* start mapper */
				
		this.getFixture().mapSDocument();
				
		
		/* comparing fixture to template */
		SDocumentGraph fixGraph = this.getFixture().getSDocument().getSDocumentGraph();
		assertNotNull(fixGraph.getSLayerByName(TCFMapperImport.LAYER_LEMMA).get(0));
		EList<SNode> fixLemma = fixGraph.getSLayerByName(TCFMapperImport.LAYER_LEMMA).get(0).getSNodes();
		String lemmaQName = SaltSemanticsPackage.eNS_PREFIX+"::"+SALT_SEMANTIC_NAMES.LEMMA.toString();
		
		assertFalse(fixLemma.isEmpty());
		assertEquals(docLemma.size(), fixLemma.size());
		SNode docNode = null;
		SNode fixNode = null;
		for(int i=0; i<docLemma.size(); i++){
			docNode = docLemma.get(i);
			fixNode = fixLemma.get(i);
			/* both of the same class? */
			assertEquals(docNode.getClass(), fixNode.getClass());
			/* both overlap the same SText? */
			assertEquals(docGraph.getSText(docNode), fixGraph.getSText(fixNode));
			/* lemma annotation exists for fixture? */
			assertNotNull(fixNode.getSAnnotation(lemmaQName));
			/* annotations are equal? */
			assertEquals(docNode.getSAnnotation(lemmaQName), fixNode.getSAnnotation(lemmaQName));
		}
	}
	
	/**
	 * This method tests if a valid TCF-XML-structure containing lemmas
	 * and tokens is converted to salt correctly by {@link TCFMapperImport}.
	 * The mapper is supposed to map annotations as {@link SLemmaAnnotation} objects
	 * directly as annotations on {@link SSpan} objects for single token annotations
	 * and in case of multiple token annotation.  
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testTokensLemmaNotShrinked() throws XMLStreamException, FileNotFoundException{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o= XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);		
		
		xmlWriter.writeStartDocument();
			xmlWriter.writeProcessingInstruction(TCFDictionary.TCF_PI);
			xmlWriter.writeStartElement(TCFDictionary.NS_WL, TCFDictionary.TAG_WL_D_SPIN, TCFDictionary.NS_VALUE_WL);
			xmlWriter.writeNamespace(TCFDictionary.NS_WL, TCFDictionary.NS_VALUE_WL);
			xmlWriter.writeNamespace(TCFDictionary.NS_ED, TCFDictionary.NS_VALUE_ED);
			xmlWriter.writeNamespace(TCFDictionary.NS_LX, TCFDictionary.NS_VALUE_LX);
			xmlWriter.writeNamespace(TCFDictionary.NS_MD, TCFDictionary.NS_VALUE_MD);
			xmlWriter.writeNamespace(TCFDictionary.NS_TC, TCFDictionary.NS_VALUE_TC);
			xmlWriter.writeAttribute(TCFDictionary.ATT_VERSION, "0.4");
				xmlWriter.writeStartElement(TCFDictionary.NS_MD, TCFDictionary.TAG_MD_METADATA, TCFDictionary.NS_VALUE_MD);
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTCORPUS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXT, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeCharacters(EXAMPLE_TEXT_SHRINK);
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKENS, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t1");
							xmlWriter.writeCharacters("I");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t2");
							xmlWriter.writeCharacters("love");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t3");
							xmlWriter.writeCharacters("New");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t4");
							xmlWriter.writeCharacters("York");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t5");
							xmlWriter.writeCharacters(".");
						xmlWriter.writeEndElement();						
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_LEMMAS, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_LEMMA, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt1");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t1");
						xmlWriter.writeCharacters("I");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_LEMMA, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt2");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t2");
						xmlWriter.writeCharacters("love");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_LEMMA, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt3");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t3 t4");
						xmlWriter.writeCharacters("New York");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_LEMMA, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt4");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t5");
						xmlWriter.writeCharacters(".");
						xmlWriter.writeEndElement();						
					xmlWriter.writeEndElement();
				xmlWriter.writeEndElement();
			xmlWriter.writeEndElement();
		xmlWriter.writeEndDocument();
		
		/* generating salt sample */
		SDocument doc = SaltFactory.eINSTANCE.createSDocument();
		doc.setSDocumentGraph(SaltFactory.eINSTANCE.createSDocumentGraph());		
		SDocumentGraph docGraph = doc.getSDocumentGraph();
		docGraph.createSTextualDS(EXAMPLE_TEXT_SHRINK);
		docGraph.tokenize();
		EList<SToken> docTokens = docGraph.getSTokens();
		SLayer docLemmaLayer = SaltFactory.eINSTANCE.createSLayer();
		docLemmaLayer.setSName(TCFMapperImport.LAYER_LEMMA);
		EList<SNode> docLemma = docLemmaLayer.getSNodes();
		
		SSpan sSpan = docGraph.createSSpan(docTokens.get(0));
		SAnnotation sAnno = SaltFactory.eINSTANCE.createSLemmaAnnotation();
		sAnno.setValue("I");
		sSpan.addSAnnotation(sAnno);
		docLemma.add(sSpan);
		
		sSpan = docGraph.createSSpan(docTokens.get(1));
		sAnno = SaltFactory.eINSTANCE.createSLemmaAnnotation();
		sAnno.setValue("love");
		sSpan.addSAnnotation(sAnno);
		docLemma.add(sSpan);
		
		sSpan = docGraph.createSSpan(docTokens.get(2));
		docGraph.addSNode(sSpan, docTokens.get(3), STYPE_NAME.SSPANNING_RELATION);
		sAnno = SaltFactory.eINSTANCE.createSLemmaAnnotation();
		sAnno.setValue("New York");
		sSpan.addSAnnotation(sAnno);
		docLemma.add(sSpan);
		
		sSpan = docGraph.createSSpan(docTokens.get(4));
		sAnno = SaltFactory.eINSTANCE.createSLemmaAnnotation();
		sAnno.setValue(".");
		sSpan.addSAnnotation(sAnno);
		docLemma.add(sSpan);		
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_TOKENS_LEMMA);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		this.getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, false);
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, SPAN_REUSE);
		
		/* start mapper */
				
		this.getFixture().mapSDocument();
				
			
		
		/* comparing fixture to template */
		SDocumentGraph fixGraph = this.getFixture().getSDocument().getSDocumentGraph();
		assertNotNull(fixGraph.getSLayerByName(TCFMapperImport.LAYER_LEMMA).get(0));
		EList<SNode> fixLemma = fixGraph.getSLayerByName(TCFMapperImport.LAYER_LEMMA).get(0).getSNodes();
		String lemmaQName = SaltSemanticsPackage.eNS_PREFIX+"::"+SALT_SEMANTIC_NAMES.LEMMA.toString();
		
		assertFalse(fixLemma.isEmpty());
		assertEquals(docLemma.size(), fixLemma.size());
		SNode docNode = null;
		SNode fixNode = null;
		for(int i=0; i<docLemma.size(); i++){
			docNode = docLemma.get(i);
			/*TEST*/fixNode = fixLemma.get(i);
			/*TEST*//* instance of class Span? */
			assertTrue(fixNode instanceof SSpan);
			/* both overlap the same SText? */
			assertEquals(docGraph.getSText(docNode), fixGraph.getSText(fixNode));
			/* lemma annotation exists for fixture? */
			assertNotNull(fixNode.getSAnnotation(lemmaQName));
			/* annotations are equal? */
			assertEquals(docNode.getSAnnotation(lemmaQName), fixNode.getSAnnotation(lemmaQName));
		}
	}
	
	/**
	 * This method tests if a valid TCF-XML-structure containing dependency
	 * annotations (no multi-governing) is converted to salt correctly by {@link TCFMapperImport} 
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testDepparsingNoMultigovs() throws XMLStreamException, FileNotFoundException{
		/*
		 * TODO test for multiGovs
		 */
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o= XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);		
		
		xmlWriter.writeStartDocument();
			xmlWriter.writeProcessingInstruction(TCFDictionary.TCF_PI);
			xmlWriter.writeStartElement(TCFDictionary.NS_WL, TCFDictionary.TAG_WL_D_SPIN, TCFDictionary.NS_VALUE_WL);
			xmlWriter.writeNamespace(TCFDictionary.NS_WL, TCFDictionary.NS_VALUE_WL);
			xmlWriter.writeNamespace(TCFDictionary.NS_ED, TCFDictionary.NS_VALUE_ED);
			xmlWriter.writeNamespace(TCFDictionary.NS_LX, TCFDictionary.NS_VALUE_LX);
			xmlWriter.writeNamespace(TCFDictionary.NS_MD, TCFDictionary.NS_VALUE_MD);
			xmlWriter.writeNamespace(TCFDictionary.NS_TC, TCFDictionary.NS_VALUE_TC);
			xmlWriter.writeAttribute(TCFDictionary.ATT_VERSION, "4.0");
				xmlWriter.writeStartElement(TCFDictionary.NS_MD, TCFDictionary.TAG_MD_METADATA, TCFDictionary.NS_VALUE_MD);
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTCORPUS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXT, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeCharacters(EXAMPLE_TEXT);
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKENS, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t1");
							xmlWriter.writeCharacters("Is");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t2");
							xmlWriter.writeCharacters("this");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t3");
							xmlWriter.writeCharacters("example");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t4");
							xmlWriter.writeCharacters("more");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t5");
							xmlWriter.writeCharacters("complicated");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t6");
							xmlWriter.writeCharacters("than");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t7");
							xmlWriter.writeCharacters("it");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t8");
							xmlWriter.writeCharacters("appears");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t9");
							xmlWriter.writeCharacters("to");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t10");
							xmlWriter.writeCharacters("be");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t11");
							xmlWriter.writeCharacters("?");
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.TAG_TC_DEPPARSING);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TAGSET, "penn treebank");
					xmlWriter.writeAttribute(TCFDictionary.ATT_EMPTYTOKS, "false");
					xmlWriter.writeAttribute(TCFDictionary.ATT_MULTIGOVS, "false");
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_PARSE);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "d_0");
							/** DEPENDENCY Is-1 **/
							xmlWriter.writeStartElement(TCFDictionary.TAG_TC_DEPENDENCY);
							xmlWriter.writeAttribute(TCFDictionary.ATT_GOVIDS, "t5");
							xmlWriter.writeAttribute(TCFDictionary.ATT_DEPIDS, "t1");
							xmlWriter.writeAttribute(TCFDictionary.ATT_FUNC, "cop");
							xmlWriter.writeEndElement();
							/** DEPENDENCY this-2 **/
							xmlWriter.writeStartElement(TCFDictionary.TAG_TC_DEPENDENCY);							
							xmlWriter.writeAttribute(TCFDictionary.ATT_GOVIDS, "t3");
							xmlWriter.writeAttribute(TCFDictionary.ATT_DEPIDS, "t2");
							xmlWriter.writeAttribute(TCFDictionary.ATT_FUNC, "det");
							xmlWriter.writeEndElement();
							/** DEPENDENCY example-3 **/
							xmlWriter.writeStartElement(TCFDictionary.TAG_TC_DEPENDENCY);							
							xmlWriter.writeAttribute(TCFDictionary.ATT_GOVIDS, "t5");
							xmlWriter.writeAttribute(TCFDictionary.ATT_DEPIDS, "t3");
							xmlWriter.writeAttribute(TCFDictionary.ATT_FUNC, "nsubj");
							xmlWriter.writeEndElement();
							/** DEPENDENCY more-4 **/
							xmlWriter.writeStartElement(TCFDictionary.TAG_TC_DEPENDENCY);							
							xmlWriter.writeAttribute(TCFDictionary.ATT_GOVIDS, "t5");
							xmlWriter.writeAttribute(TCFDictionary.ATT_DEPIDS, "t4");
							xmlWriter.writeAttribute(TCFDictionary.ATT_FUNC, "advmod");
							xmlWriter.writeEndElement();
							/** DEPENDENCY complicated-5 **/
							xmlWriter.writeStartElement(TCFDictionary.TAG_TC_DEPENDENCY);
							xmlWriter.writeAttribute(TCFDictionary.ATT_DEPIDS, "t5");
							xmlWriter.writeAttribute(TCFDictionary.ATT_FUNC, "root");
							xmlWriter.writeEndElement();
							/** DEPENDENCY than-6 **/
							xmlWriter.writeStartElement(TCFDictionary.TAG_TC_DEPENDENCY);							
							xmlWriter.writeAttribute(TCFDictionary.ATT_GOVIDS, "t8");
							xmlWriter.writeAttribute(TCFDictionary.ATT_DEPIDS, "t6");
							xmlWriter.writeAttribute(TCFDictionary.ATT_FUNC, "mark");
							xmlWriter.writeEndElement();
							/** DEPENDENCY it-7 **/
							xmlWriter.writeStartElement(TCFDictionary.TAG_TC_DEPENDENCY);							
							xmlWriter.writeAttribute(TCFDictionary.ATT_GOVIDS, "t8");
							xmlWriter.writeAttribute(TCFDictionary.ATT_DEPIDS, "t7");
							xmlWriter.writeAttribute(TCFDictionary.ATT_FUNC, "nsubj");
							xmlWriter.writeEndElement();
							/** DEPENDENCY appears-8 **/
							xmlWriter.writeStartElement(TCFDictionary.TAG_TC_DEPENDENCY);							
							xmlWriter.writeAttribute(TCFDictionary.ATT_GOVIDS, "t5");
							xmlWriter.writeAttribute(TCFDictionary.ATT_DEPIDS, "t8");
							xmlWriter.writeAttribute(TCFDictionary.ATT_FUNC, "advcl");
							xmlWriter.writeEndElement();
							/** DEPENDENCY to-9 **/
							xmlWriter.writeStartElement(TCFDictionary.TAG_TC_DEPENDENCY);							
							xmlWriter.writeAttribute(TCFDictionary.ATT_GOVIDS, "t10");
							xmlWriter.writeAttribute(TCFDictionary.ATT_DEPIDS, "t9");
							xmlWriter.writeAttribute(TCFDictionary.ATT_FUNC, "aux");
							xmlWriter.writeEndElement();
							/** DEPENDENCY be-10 **/
							xmlWriter.writeStartElement(TCFDictionary.TAG_TC_DEPENDENCY);							
							xmlWriter.writeAttribute(TCFDictionary.ATT_GOVIDS, "t8");
							xmlWriter.writeAttribute(TCFDictionary.ATT_DEPIDS, "t10");
							xmlWriter.writeAttribute(TCFDictionary.ATT_FUNC, "xcomp");
							xmlWriter.writeEndElement();
							/** DEPENDENCY ?-11 **/
							xmlWriter.writeStartElement(TCFDictionary.TAG_TC_DEPENDENCY);
							xmlWriter.writeAttribute(TCFDictionary.ATT_DEPIDS, "t11");
							xmlWriter.writeAttribute(TCFDictionary.ATT_FUNC, "root");
							xmlWriter.writeEndElement();
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();
				xmlWriter.writeEndElement();
			xmlWriter.writeEndElement();
		xmlWriter.writeEndDocument();
		
		/* generating salt sample */
		SDocument doc = SaltFactory.eINSTANCE.createSDocument();
		doc.setSDocumentGraph(SaltFactory.eINSTANCE.createSDocumentGraph());
		SaltSample.createPrimaryData(doc);
		SaltSample.createTokens2(doc);
		SaltSample.createDependencies(doc);
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_DEPENDENCIES_NO_MULTIGOVS);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		this.getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, SPAN_REUSE);
		
		/* start mapper */
				
		this.getFixture().mapSDocument();
				
		/* tests from other methods */
		
		/**
		 * TODO (?)
		 */
		
		/* -- compare template salt model to imported salt model -- 
		 * 
		 * in this test we try to be independent of the order of
		 * the elements in their lists, which makes the test on
		 * the one hand more general, on the other hand more
		 * complicated
		 * */	
		SDocumentGraph fixGraph = getFixture().getSDocument().getSDocumentGraph();
		SDocumentGraph docGraph = doc.getSDocumentGraph();
		EList<SPointingRelation> docPRels = docGraph.getSPointingRelations();
		EList<SPointingRelation> fixPRels = fixGraph.getSPointingRelations();		
		
		assertNotNull(fixPRels);
		assertNotEquals(0, fixPRels.size());		
		assertEquals(docPRels.size(), fixPRels.size());
		for(int i=0; i<docPRels.size(); i++){
			assertNotNull(docPRels.get(i).getSSource());
			assertNotNull(docPRels.get(i).getSTarget());
			assertNotNull(fixPRels.get(i).getSSource());
			assertNotNull(fixPRels.get(i).getSTarget());
		}
		
		/* collect all tokens + their String values */
		
		EMap<SToken, String> docTokensText = new BasicEMap<SToken, String>();
		EMap<SToken, String> fixTokensText = new BasicEMap<SToken, String>();
		EList<SToken> orderedFixTokens = new BasicEList<SToken>();		
		
		SToken sTok = null;
		for(STextualRelation txtRel : docGraph.getSTextualRelations()){			
			sTok = (SToken)txtRel.getSSource();
			docTokensText.put(sTok, docGraph.getSTextualDSs().get(0).getSText().substring(txtRel.getSStart(), txtRel.getSEnd()));
		}
		
		for(STextualRelation txtRel : fixGraph.getSTextualRelations()){
			sTok = (SToken)txtRel.getSSource();
			orderedFixTokens.add(sTok);
			fixTokensText.put(sTok, fixGraph.getSTextualDSs().get(0).getSText().substring(txtRel.getSStart(), txtRel.getSEnd()));			
		}		
		
		/* check dependencies
		 * 
		 * we have to do it in the linear order of the tokens,
		 * because the doubling of both tokens (String) AND
		 * dependencies (String) is possible 
		 * */
		SToken fixTok = null;
		for(SToken docTok : docGraph.getSortedSTokenByText()){
			/* find SPointingRelation for docTok */
			int j = 0;
			boolean isGoverned = true;
			while(!docPRels.get(j).getSTarget().equals(docTok)){
				assertNotNull(docPRels.get(j).getSTarget());
				if(docPRels.size()==j+1){
					isGoverned = false;
					break;
				}
				j++;
			}
			if(isGoverned){				
				/* find equivalent token in fixture */
				int t = 0;
				while(!fixTokensText.get(orderedFixTokens.get(t)).equals(docTokensText.get(docTok))){
					t++;
				}
				fixTok = orderedFixTokens.get(t);
				orderedFixTokens.remove(fixTok);
				/* find dependency governing fixTok */
				int k = 0;
				while(!fixPRels.get(k).getSTarget().equals(fixTok)){
					assertNotNull(fixPRels.get(k).getSTarget());
					k++;					
				}
				/* check dependency */
				assertEquals(docPRels.get(j).getSAnnotation("dependency").getSValue(), fixPRels.get(j).getSAnnotation(TCFMapperImport.LAYER_DEPENDENCIES+"::"+TCFDictionary.ATT_FUNC).getSValue());				
			}
		}
	}
	
	/**This method tests if a valid TCF-XML-structure containing constituent
	 * annotations is converted to salt correctly by {@link TCFMapperImport} 
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testConstituentParsing() throws XMLStreamException, FileNotFoundException{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o= XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);
		
		xmlWriter.writeStartDocument();
		xmlWriter.writeProcessingInstruction(TCFDictionary.TCF_PI);
			xmlWriter.writeStartElement(TCFDictionary.NS_WL, TCFDictionary.TAG_WL_D_SPIN, TCFDictionary.NS_VALUE_WL);
			xmlWriter.writeNamespace(TCFDictionary.NS_WL, TCFDictionary.NS_VALUE_WL);
			xmlWriter.writeNamespace(TCFDictionary.NS_ED, TCFDictionary.NS_VALUE_ED);
			xmlWriter.writeNamespace(TCFDictionary.NS_LX, TCFDictionary.NS_VALUE_LX);
			xmlWriter.writeNamespace(TCFDictionary.NS_MD, TCFDictionary.NS_VALUE_MD);
			xmlWriter.writeNamespace(TCFDictionary.NS_TC, TCFDictionary.NS_VALUE_TC);
			xmlWriter.writeAttribute(TCFDictionary.ATT_VERSION, "4.0");
				xmlWriter.writeStartElement(TCFDictionary.NS_MD, TCFDictionary.TAG_MD_METADATA, TCFDictionary.NS_VALUE_MD);
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTCORPUS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXT, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeCharacters(EXAMPLE_TEXT);
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKENS, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t1");
							xmlWriter.writeCharacters("Is");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t2");
							xmlWriter.writeCharacters("this");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t3");
							xmlWriter.writeCharacters("example");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t4");
							xmlWriter.writeCharacters("more");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t5");
							xmlWriter.writeCharacters("complicated");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t6");
							xmlWriter.writeCharacters("than");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t7");
							xmlWriter.writeCharacters("it");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t8");
							xmlWriter.writeCharacters("appears");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t9");
							xmlWriter.writeCharacters("to");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t10");
							xmlWriter.writeCharacters("be");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t11");
							xmlWriter.writeCharacters("?");
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.TAG_TC_PARSING);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TAGSET, "unknown");
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_PARSE, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONSTITUENT, TCFDictionary.NS_VALUE_TC);//root node
							xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "ROOT");
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "c1");
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONSTITUENT, TCFDictionary.NS_VALUE_TC);//sq
								xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "SQ");
								xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "c2");
									xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONSTITUENT, TCFDictionary.NS_VALUE_TC);//"Is"
									xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "VBZ");
									xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "c3");
									xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t1");
									xmlWriter.writeEndElement();//End of "is"
									xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONSTITUENT, TCFDictionary.NS_VALUE_TC);//np1
									xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "NP");
									xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "c4");
										xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONSTITUENT, TCFDictionary.NS_VALUE_TC);//"this"
										xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "DT");
										xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "c5");
										xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t2");
										xmlWriter.writeEndElement();//End of "this"
										xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONSTITUENT, TCFDictionary.NS_VALUE_TC);//"example"
										xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "NN");
										xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "c6");
										xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t3");
										xmlWriter.writeEndElement();//End of "example"
									xmlWriter.writeEndElement();//End of np1
									xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONSTITUENT, TCFDictionary.NS_VALUE_TC);//adjp1
									xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "ADJP");
									xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "c7");
										xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONSTITUENT, TCFDictionary.NS_VALUE_TC);//adjp2
										xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "ADJP");
										xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "c8");
											xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONSTITUENT, TCFDictionary.NS_VALUE_TC);//"more"
											xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "RBR");
											xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "c9");
											xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t4");
											xmlWriter.writeEndElement();//End of "more"
											xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONSTITUENT, TCFDictionary.NS_VALUE_TC);//"complicated"
											xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "JJ");
											xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "c10");
											xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t5");
											xmlWriter.writeEndElement();//End of "complicated"
										xmlWriter.writeEndElement();//End of adjp2
										xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONSTITUENT, TCFDictionary.NS_VALUE_TC);//sbar
										xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "SBar");
										xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "c11");
											xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONSTITUENT, TCFDictionary.NS_VALUE_TC);//"than"
											xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "IN");
											xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "c12");
											xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t6");
											xmlWriter.writeEndElement();//End of "than"
											xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONSTITUENT, TCFDictionary.NS_VALUE_TC);//s1
											xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "S");
											xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "c13");
												xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONSTITUENT, TCFDictionary.NS_VALUE_TC);//np2
												xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "NP");
												xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "c14");
													xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONSTITUENT, TCFDictionary.NS_VALUE_TC);//"it"
													xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "PRP");
													xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "c15");
													xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t7");
													xmlWriter.writeEndElement();//End of "it"
												xmlWriter.writeEndElement();//End of np2
												xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONSTITUENT, TCFDictionary.NS_VALUE_TC);//vp1
												xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "VP");
												xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "c16");
													xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONSTITUENT, TCFDictionary.NS_VALUE_TC);//"appears"
													xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "VBZ");
													xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "c17");
													xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t8");
													xmlWriter.writeEndElement();//End of "appears"
													xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONSTITUENT, TCFDictionary.NS_VALUE_TC);//s2
													xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "S");
													xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "c18");
														xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONSTITUENT, TCFDictionary.NS_VALUE_TC);//vp2
														xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "VP");
														xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "c19");
															xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONSTITUENT, TCFDictionary.NS_VALUE_TC);//"to"
															xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "TO");
															xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "c20");
															xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t9");
															xmlWriter.writeEndElement();//End of "to"
															xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONSTITUENT, TCFDictionary.NS_VALUE_TC);//vp3
															xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "VP");
															xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "c21");
																xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONSTITUENT, TCFDictionary.NS_VALUE_TC);//"be"
																xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "VB");
																xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "c22");
																xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t10");
																xmlWriter.writeEndElement();//End of "be"	
															xmlWriter.writeEndElement();//End of vp3
														xmlWriter.writeEndElement();//End of vp2
													xmlWriter.writeEndElement();//End of s2
												xmlWriter.writeEndElement();//End of vp1
											xmlWriter.writeEndElement();//End of s1											
										xmlWriter.writeEndElement();//End of sbar
									xmlWriter.writeEndElement();//End of adjp1
								xmlWriter.writeEndElement();//End of sq
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONSTITUENT, TCFDictionary.NS_VALUE_TC);//"?"
								xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, ".");
								xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "c23");
								xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t11");
								xmlWriter.writeEndElement();//End of "?"
							xmlWriter.writeEndElement();//End of root node
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();
				xmlWriter.writeEndElement();//End of TextCorpus
			xmlWriter.writeEndElement();
		xmlWriter.writeEndDocument();
		
		/* generating salt sample */
		SDocument doc = SaltFactory.eINSTANCE.createSDocument();
		doc.setSDocumentGraph(SaltFactory.eINSTANCE.createSDocumentGraph());
		SaltSample.createPrimaryData(doc);
		SaltSample.createTokens2(doc);
		/* TODO create Version 2 of both the following methods --> Root node necessary to include punctuation */
		/* think about some kind of flag to include/exclude the question mark */
		SaltSample.createSyntaxStructure2(doc);
		SaltSample.createSyntaxAnnotations2(doc);
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_CONSTITUENT_PARSING);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		this.getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, SPAN_REUSE);
		
		/* start mapper */
				
		this.getFixture().mapSDocument();
				
		
		/* -- compare template salt model to imported salt model -- */
		
		SDocumentGraph docGraph = doc.getSDocumentGraph();
		SDocumentGraph fixGraph = getFixture().getSDocument().getSDocumentGraph();
		
		assertNotEquals(fixGraph.getSLayerByName(TCFMapperImport.LAYER_CONSTITUENTS).size(), 0);	
		
		SLayer docSynLayer = docGraph.getSLayerByName(TCFMapperImport.LAYER_CONSTITUENTS).get(0);		
		SLayer fixSynLayer = fixGraph.getSLayerByName(TCFMapperImport.LAYER_CONSTITUENTS).get(0);
		
		assertEquals(docSynLayer.getAllIncludedNodes().size(), fixSynLayer.getAllIncludedNodes().size());
				
		EList<SNode> docNodes = docGraph.getSLayerByName(TCFMapperImport.LAYER_CONSTITUENTS).get(0).getSNodes();
		EList<SNode> fixNodes = fixGraph.getSLayerByName(TCFMapperImport.LAYER_CONSTITUENTS).get(0).getSNodes();
		for(int i=0; i<docNodes.size(); i++){			
//			//			//			//			assertEquals(docNodes.get(i).getSElementId(), fixNodes.get(i).getSElementId());
			assertEquals(docGraph.getSText(docNodes.get(i)), fixGraph.getSText(fixNodes.get(i)));
			assertEquals(docNodes.get(i).getSAnnotation(TCFMapperImport.ANNO_NAME_CONSTITUENT).getValue(), fixNodes.get(i).getSAnnotation(TCFMapperImport.LAYER_CONSTITUENTS+"::"+TCFDictionary.ATT_CAT).getValue());
		}
	}
		
	/**This method tests if a valid TCF-XML-structure containing constituent
	 * annotations is converted to salt correctly by {@link TCFMapperImport}.  
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testConstituentParsingShrinked() throws FileNotFoundException, XMLStreamException{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o= XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);
		
		xmlWriter.writeStartDocument();
		xmlWriter.writeProcessingInstruction(TCFDictionary.TCF_PI);
			xmlWriter.writeStartElement(TCFDictionary.NS_WL, TCFDictionary.TAG_WL_D_SPIN, TCFDictionary.NS_VALUE_WL);
			xmlWriter.writeNamespace(TCFDictionary.NS_WL, TCFDictionary.NS_VALUE_WL);
			xmlWriter.writeNamespace(TCFDictionary.NS_ED, TCFDictionary.NS_VALUE_ED);
			xmlWriter.writeNamespace(TCFDictionary.NS_LX, TCFDictionary.NS_VALUE_LX);
			xmlWriter.writeNamespace(TCFDictionary.NS_MD, TCFDictionary.NS_VALUE_MD);
			xmlWriter.writeNamespace(TCFDictionary.NS_TC, TCFDictionary.NS_VALUE_TC);
			xmlWriter.writeAttribute(TCFDictionary.ATT_VERSION, "0.4");
				xmlWriter.writeStartElement(TCFDictionary.NS_MD, TCFDictionary.TAG_MD_METADATA, TCFDictionary.NS_VALUE_MD);
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTCORPUS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXT, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeCharacters(EXAMPLE_TEXT_SHRINK);
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKENS, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t1");
							xmlWriter.writeCharacters("I");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t2");
							xmlWriter.writeCharacters("love");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t3");
							xmlWriter.writeCharacters("New");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t4");
							xmlWriter.writeCharacters("York");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t5");
							xmlWriter.writeCharacters(".");
						xmlWriter.writeEndElement();						
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.TAG_TC_PARSING);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TAGSET, "unknown");
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_PARSE, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONSTITUENT, TCFDictionary.NS_VALUE_TC);//root node
							xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "ROOT");
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "c1");
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONSTITUENT, TCFDictionary.NS_VALUE_TC);//S
								xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "S");
								xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "c2");
									xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONSTITUENT, TCFDictionary.NS_VALUE_TC);//NP
									xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "NP");
									xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "c3");
										xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONSTITUENT, TCFDictionary.NS_VALUE_TC);//"I"
										xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "PP");
										xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "c4");
										xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t1");
										xmlWriter.writeEndElement();//End of "I"
									xmlWriter.writeEndElement();//End of NP
									xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONSTITUENT, TCFDictionary.NS_VALUE_TC);//"love"
									xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "VBZ");
									xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "c5");
									xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t2");
									xmlWriter.writeEndElement();//End of "love"
									xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONSTITUENT, TCFDictionary.NS_VALUE_TC);//NP
									xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "NP");
									xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "c6");
										xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONSTITUENT, TCFDictionary.NS_VALUE_TC);//"New York"
										xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "NNP");
										xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "c7");
										xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t3 t4");
										xmlWriter.writeEndElement();//End of "New York"
									xmlWriter.writeEndElement();//End of NP
								xmlWriter.writeEndElement();//End of S
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONSTITUENT, TCFDictionary.NS_VALUE_TC);//"."
								xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, ".");
								xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "c8");
								xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t5");
								xmlWriter.writeEndElement();//End of "."
							xmlWriter.writeEndElement();//End of root node
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();
				xmlWriter.writeEndElement();//End of TextCorpus
			xmlWriter.writeEndElement();
		xmlWriter.writeEndDocument();
		
		/* generating salt sample */
		SDocument doc = SaltFactory.eINSTANCE.createSDocument();
		doc.setSDocumentGraph(SaltFactory.eINSTANCE.createSDocumentGraph());
		SDocumentGraph docGraph = doc.getSDocumentGraph();
		docGraph.createSTextualDS(EXAMPLE_TEXT_SHRINK);
		docGraph.tokenize();
		EList<SToken> docTokens = docGraph.getSTokens();
		SLayer docSynLayer = SaltFactory.eINSTANCE.createSLayer(); 
		docGraph.addSLayer(docSynLayer);
		docSynLayer.setSName(TCFMapperImport.LAYER_CONSTITUENTS);
		EList<SNode> docConstituents = docSynLayer.getSNodes();	
		
		SStructure root = SaltFactory.eINSTANCE.createSStructure();//ROOT
		root.createSAnnotation(TCFMapperImport.LAYER_CONSTITUENTS, TCFDictionary.ATT_CAT, "ROOT");
		SStructure s = SaltFactory.eINSTANCE.createSStructure();//S
		s.createSAnnotation(TCFMapperImport.LAYER_CONSTITUENTS, TCFDictionary.ATT_CAT, "S");
		SStructure np1 = SaltFactory.eINSTANCE.createSStructure();//NP(1)
		np1.createSAnnotation(TCFMapperImport.LAYER_CONSTITUENTS, TCFDictionary.ATT_CAT, "NP");
		SStructure np2 = SaltFactory.eINSTANCE.createSStructure();//NP(2)
		np2.createSAnnotation(TCFMapperImport.LAYER_CONSTITUENTS, TCFDictionary.ATT_CAT, "NP");
		SSpan newYork = docGraph.createSSpan(docTokens.get(2));
		docGraph.addSNode(newYork, docTokens.get(3), STYPE_NAME.SSPANNING_RELATION);
		newYork.createSAnnotation(TCFMapperImport.LAYER_CONSTITUENTS, TCFDictionary.ATT_CAT, "NNP");
		
		docGraph.addSNode(root);
		docGraph.addSNode(root, s, STYPE_NAME.SDOMINANCE_RELATION);
		docGraph.addSNode(s, np1, STYPE_NAME.SDOMINANCE_RELATION);
		docGraph.addSNode(np1, docTokens.get(0), STYPE_NAME.SDOMINANCE_RELATION);
		docGraph.addSNode(s, docTokens.get(1), STYPE_NAME.SDOMINANCE_RELATION);
		docGraph.addSNode(s, np2, STYPE_NAME.SDOMINANCE_RELATION);
		docGraph.addSNode(np2, newYork, STYPE_NAME.SDOMINANCE_RELATION);
		docGraph.addSNode(root, docTokens.get(4), STYPE_NAME.SDOMINANCE_RELATION);
		
		docConstituents.add(root);
		docConstituents.add(s);
		docConstituents.add(np1);
		docConstituents.add(np2);
		
		docTokens.get(0).createSAnnotation(TCFMapperImport.LAYER_CONSTITUENTS, TCFDictionary.ATT_CAT, "PP");
		docTokens.get(1).createSAnnotation(TCFMapperImport.LAYER_CONSTITUENTS, TCFDictionary.ATT_CAT, "VBZ");
		docTokens.get(4).createSAnnotation(TCFMapperImport.LAYER_CONSTITUENTS, TCFDictionary.ATT_CAT, ".");
		/* spans and tokens do not belong to the constituent layer */
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_CONSTITUENT_PARSING);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		this.getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, true);
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, SPAN_REUSE);
		
		/* start mapper */
				
		this.getFixture().mapSDocument();
				
		
		/* -- compare template salt model to imported salt model -- */
		
		SDocumentGraph fixGraph = getFixture().getSDocument().getSDocumentGraph();		
		assertNotEquals(fixGraph.getSLayerByName(TCFMapperImport.LAYER_CONSTITUENTS).size(), 0);		
				
		EList<SNode> fixConstituents = fixGraph.getSLayerByName(TCFMapperImport.LAYER_CONSTITUENTS).get(0).getSNodes();
		assertEquals(docConstituents.size(), fixConstituents.size());		
		
		SNode docNode = null;
		SNode fixNode = null;
		EList<SToken> docOTokens = null;
		EList<SToken> fixOTokens = null;
		EList<STYPE_NAME> domRelType = new BasicEList<STYPE_NAME>();
		domRelType.add(STYPE_NAME.SDOMINANCE_RELATION);
		if(DEBUG){}
		for(int i=0; i<docConstituents.size(); i++){			
			docNode = docConstituents.get(i);
			fixNode = fixConstituents.get(i);
			if(DEBUG){
				}
			assertEquals(docNode.getSElementId(), fixNode.getSElementId());
			assertEquals(docNode.getClass(), fixNode.getClass());
			assertEquals(docGraph.getSText(docNode), fixGraph.getSText(fixNode));
			assertEquals(docNode.getSAnnotation(TCFMapperImport.LAYER_CONSTITUENTS+"::"+TCFDictionary.ATT_CAT), fixNode.getSAnnotation(TCFMapperImport.LAYER_CONSTITUENTS+"::"+TCFDictionary.ATT_CAT));
			docOTokens = docGraph.getOverlappedSTokens(docNode, domRelType);
			fixOTokens = fixGraph.getOverlappedSTokens(fixNode, domRelType);
			assertEquals(docOTokens.size(), fixOTokens.size());
			for(int j=0; j<docOTokens.size(); j++){
				docNode = docOTokens.get(j);
				fixNode = fixOTokens.get(j);
				/* compare annotations? */
				assertEquals(docNode.getSAnnotation(TCFMapperImport.LAYER_CONSTITUENTS+"::"+TCFDictionary.ATT_CAT).getValue(), fixNode.getSAnnotation(TCFMapperImport.LAYER_CONSTITUENTS+"::"+TCFDictionary.ATT_CAT).getValue());				
				/* compare text? */
				assertEquals(docGraph.getSText(docNode), fixGraph.getSText(fixNode));
			}
		}
		/* compare spans: */
		EList<SSpan> docSpans = docGraph.getSSpans();
		EList<SSpan> fixSpans = fixGraph.getSSpans();
		assertNotNull(fixSpans);
		assertFalse(fixSpans.isEmpty());
		assertEquals(docSpans.size(), fixSpans.size());
		if(DEBUG){}
		for(int i=0; i<docSpans.size(); i++){
			docNode = docSpans.get(i);
			fixNode = fixSpans.get(i);			
			if(DEBUG){
				}
			assertEquals(docNode.getSElementId(), fixNode.getSElementId());
			if(DEBUG){}
			assertEquals(docGraph.getSText(docNode), fixGraph.getSText(fixNode));
			assertNotNull(fixNode.getSAnnotation(TCFMapperImport.LAYER_CONSTITUENTS+"::"+TCFDictionary.ATT_CAT));
			if(DEBUG){}			
			assertEquals(docNode.getSAnnotation(TCFMapperImport.LAYER_CONSTITUENTS+"::"+TCFDictionary.ATT_CAT).getValue(), fixNode.getSAnnotation(TCFMapperImport.LAYER_CONSTITUENTS+"::"+TCFDictionary.ATT_CAT).getValue());
		}
	}
	
	/**This method tests if a valid TCF-XML-structure containing constituent
	 * annotations is converted to salt correctly by {@link TCFMapperImport}.  
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testConstituentParsingNotShrinked() throws FileNotFoundException, XMLStreamException{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o= XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);
		
		xmlWriter.writeStartDocument();
		xmlWriter.writeProcessingInstruction(TCFDictionary.TCF_PI);
			xmlWriter.writeStartElement(TCFDictionary.NS_WL, TCFDictionary.TAG_WL_D_SPIN, TCFDictionary.NS_VALUE_WL);
			xmlWriter.writeNamespace(TCFDictionary.NS_WL, TCFDictionary.NS_VALUE_WL);
			xmlWriter.writeNamespace(TCFDictionary.NS_ED, TCFDictionary.NS_VALUE_ED);
			xmlWriter.writeNamespace(TCFDictionary.NS_LX, TCFDictionary.NS_VALUE_LX);
			xmlWriter.writeNamespace(TCFDictionary.NS_MD, TCFDictionary.NS_VALUE_MD);
			xmlWriter.writeNamespace(TCFDictionary.NS_TC, TCFDictionary.NS_VALUE_TC);
			xmlWriter.writeAttribute(TCFDictionary.ATT_VERSION, "4.0");
				xmlWriter.writeStartElement(TCFDictionary.NS_MD, TCFDictionary.TAG_MD_METADATA, TCFDictionary.NS_VALUE_MD);
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTCORPUS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXT, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeCharacters(EXAMPLE_TEXT_SHRINK);
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKENS, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t1");
							xmlWriter.writeCharacters("I");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t2");
							xmlWriter.writeCharacters("love");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t3");
							xmlWriter.writeCharacters("New");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t4");
							xmlWriter.writeCharacters("York");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t5");
							xmlWriter.writeCharacters(".");
						xmlWriter.writeEndElement();						
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.TAG_TC_PARSING);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TAGSET, "unknown");
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_PARSE, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONSTITUENT, TCFDictionary.NS_VALUE_TC);//root node
							xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "ROOT");
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "c1");
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONSTITUENT, TCFDictionary.NS_VALUE_TC);//S
								xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "S");
								xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "c2");
									xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONSTITUENT, TCFDictionary.NS_VALUE_TC);//NP
									xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "NP");
									xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "c3");
										xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONSTITUENT, TCFDictionary.NS_VALUE_TC);//"I"
										xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "PP");
										xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "c4");
										xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t1");
										xmlWriter.writeEndElement();//End of "I"
									xmlWriter.writeEndElement();//End of NP
									xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONSTITUENT, TCFDictionary.NS_VALUE_TC);//"love"
									xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "VBZ");
									xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "c5");
									xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t2");
									xmlWriter.writeEndElement();//End of "love"
									xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONSTITUENT, TCFDictionary.NS_VALUE_TC);//NP
									xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "NP");
									xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "c6");
										xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONSTITUENT, TCFDictionary.NS_VALUE_TC);//"New York"
										xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "NNP");
										xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "c7");
										xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t3 t4");
										xmlWriter.writeEndElement();//End of "New York"
									xmlWriter.writeEndElement();//End of NP
								xmlWriter.writeEndElement();//End of S
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONSTITUENT, TCFDictionary.NS_VALUE_TC);//"."
								xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, ".");
								xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "c8");
								xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t5");
								xmlWriter.writeEndElement();//End of "."
							xmlWriter.writeEndElement();//End of root node
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();
				xmlWriter.writeEndElement();//End of TextCorpus
			xmlWriter.writeEndElement();
		xmlWriter.writeEndDocument();
		
		/* generating salt sample */
		SDocument doc = SaltFactory.eINSTANCE.createSDocument();
		doc.setSDocumentGraph(SaltFactory.eINSTANCE.createSDocumentGraph());
		SDocumentGraph docGraph = doc.getSDocumentGraph();
		docGraph.createSTextualDS(EXAMPLE_TEXT_SHRINK);
		docGraph.tokenize();
		EList<SToken> docTokens = docGraph.getSTokens();
		SLayer docSynLayer = SaltFactory.eINSTANCE.createSLayer(); 
		docGraph.addSLayer(docSynLayer);
		docSynLayer.setSName(TCFMapperImport.LAYER_CONSTITUENTS);
		EList<SNode> docConstituents = docSynLayer.getSNodes();	
		
		SSpan sI = docGraph.createSSpan(docTokens.get(0));
		sI.createSAnnotation(TCFMapperImport.LAYER_CONSTITUENTS, TCFDictionary.ATT_CAT, "PP");
		
		SSpan sLove = docGraph.createSSpan(docTokens.get(1));
		sLove.createSAnnotation(TCFMapperImport.LAYER_CONSTITUENTS, TCFDictionary.ATT_CAT, "VBZ");
		
		SSpan sNewYork = docGraph.createSSpan(docTokens.get(2));		
		docGraph.addSNode(sNewYork, docTokens.get(3), STYPE_NAME.SSPANNING_RELATION);
		sNewYork.createSAnnotation(TCFMapperImport.LAYER_CONSTITUENTS, TCFDictionary.ATT_CAT, "NNP");
		
		SSpan sStop = docGraph.createSSpan(docTokens.get(4));
		sStop.createSAnnotation(TCFMapperImport.LAYER_CONSTITUENTS, TCFDictionary.ATT_CAT, ".");
		
		SStructure root = SaltFactory.eINSTANCE.createSStructure();//ROOT
		root.createSAnnotation(TCFMapperImport.LAYER_CONSTITUENTS, TCFDictionary.ATT_CAT, "ROOT");
		SStructure s = SaltFactory.eINSTANCE.createSStructure();//S
		s.createSAnnotation(TCFMapperImport.LAYER_CONSTITUENTS, TCFDictionary.ATT_CAT, "S");
		SStructure np1 = SaltFactory.eINSTANCE.createSStructure();//NP(1)
		np1.createSAnnotation(TCFMapperImport.LAYER_CONSTITUENTS, TCFDictionary.ATT_CAT, "NP");
		SStructure np2 = SaltFactory.eINSTANCE.createSStructure();//NP(2)
		np2.createSAnnotation(TCFMapperImport.LAYER_CONSTITUENTS, TCFDictionary.ATT_CAT, "NP");		
		
		docGraph.addSNode(root);
		docGraph.addSNode(root, s, STYPE_NAME.SDOMINANCE_RELATION);
		docGraph.addSNode(s, np1, STYPE_NAME.SDOMINANCE_RELATION);
		docGraph.addSNode(np1, sI, STYPE_NAME.SDOMINANCE_RELATION);
		docGraph.addSNode(s, sLove, STYPE_NAME.SDOMINANCE_RELATION);
		docGraph.addSNode(s, np2, STYPE_NAME.SDOMINANCE_RELATION);
		docGraph.addSNode(np2, sNewYork, STYPE_NAME.SDOMINANCE_RELATION);
		docGraph.addSNode(root, sStop, STYPE_NAME.SDOMINANCE_RELATION);
		
		docConstituents.add(root);
		docConstituents.add(s);
		docConstituents.add(np1);
		docConstituents.add(np2);
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_CONSTITUENT_PARSING);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		this.getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, false);
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, SPAN_REUSE);
		
		/* start mapper */
				
		this.getFixture().mapSDocument();
				
		
		/* -- compare template salt model to imported salt model -- */
		
		SDocumentGraph fixGraph = getFixture().getSDocument().getSDocumentGraph();		
		assertNotEquals(fixGraph.getSLayerByName(TCFMapperImport.LAYER_CONSTITUENTS).size(), 0);		
				
		EList<SNode> fixConstituents = fixGraph.getSLayerByName(TCFMapperImport.LAYER_CONSTITUENTS).get(0).getSNodes();
		assertEquals(docConstituents.size(), fixConstituents.size());		
		
		SNode docNode = null;
		SNode fixNode = null;
		EList<STYPE_NAME> domRelType = new BasicEList<STYPE_NAME>();
		domRelType.add(STYPE_NAME.SDOMINANCE_RELATION);
		if(DEBUG){}
		for(int i=0; i<docConstituents.size(); i++){			
			docNode = docConstituents.get(i);
			fixNode = fixConstituents.get(i);
			if(DEBUG){
				}
			assertEquals(docNode.getSElementId(), fixNode.getSElementId());
			assertEquals(docNode.getClass(), fixNode.getClass());
			assertEquals(docGraph.getSText(docNode), fixGraph.getSText(fixNode));
			assertEquals(docNode.getSAnnotation(TCFMapperImport.LAYER_CONSTITUENTS+"::"+TCFDictionary.ATT_CAT).getValue(), fixNode.getSAnnotation(TCFMapperImport.LAYER_CONSTITUENTS+"::"+TCFDictionary.ATT_CAT).getValue());			
		}
		/* compare spans: */
		EList<SSpan> docSpans = docGraph.getSSpans();
		EList<SSpan> fixSpans = fixGraph.getSSpans();
		assertNotNull(fixSpans);
		assertFalse(fixSpans.isEmpty());
		assertEquals(docSpans.size(), fixSpans.size());
		if(DEBUG){}
		for(int i=0; i<docSpans.size(); i++){
			docNode = docSpans.get(i);
			fixNode = fixSpans.get(i);			
			if(DEBUG){
				}
			assertEquals(docNode.getSElementId(), fixNode.getSElementId());
			if(DEBUG){}
			assertEquals(docGraph.getSText(docNode), fixGraph.getSText(fixNode));
			assertNotNull(fixNode.getSAnnotation(TCFMapperImport.LAYER_CONSTITUENTS+"::"+TCFDictionary.ATT_CAT));
			if(DEBUG){}			
			assertEquals(docNode.getSAnnotation(TCFMapperImport.LAYER_CONSTITUENTS+"::"+TCFDictionary.ATT_CAT).getValue(), fixNode.getSAnnotation(TCFMapperImport.LAYER_CONSTITUENTS+"::"+TCFDictionary.ATT_CAT).getValue());
		}
	}
	
	/**This method tests if a valid TCF-XML-structure containing morphology
	 * annotations is converted to salt correctly by {@link TCFMapperImport}. The Mapper
	 * is supposed to build a span for both morphology annotations on single and on multiple
	 * {@link SToken} objects. 
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testMorphologyNotShrinked() throws XMLStreamException, FileNotFoundException{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o= XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);
		
		xmlWriter.writeStartDocument();
		xmlWriter.writeProcessingInstruction(TCFDictionary.TCF_PI);
		xmlWriter.writeStartElement(TCFDictionary.NS_WL, TCFDictionary.TAG_WL_D_SPIN, TCFDictionary.NS_VALUE_WL);
		xmlWriter.writeNamespace(TCFDictionary.NS_WL, TCFDictionary.NS_VALUE_WL);
		xmlWriter.writeNamespace(TCFDictionary.NS_ED, TCFDictionary.NS_VALUE_ED);
		xmlWriter.writeNamespace(TCFDictionary.NS_LX, TCFDictionary.NS_VALUE_LX);
		xmlWriter.writeNamespace(TCFDictionary.NS_MD, TCFDictionary.NS_VALUE_MD);
		xmlWriter.writeNamespace(TCFDictionary.NS_TC, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_VERSION, "4.0");
			xmlWriter.writeStartElement(TCFDictionary.NS_MD, TCFDictionary.TAG_MD_METADATA, TCFDictionary.NS_VALUE_MD);
			xmlWriter.writeEndElement();
			xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTCORPUS, TCFDictionary.NS_VALUE_TC);
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXT, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeCharacters(EXAMPLE_TEXT);
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKENS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t1");
						xmlWriter.writeCharacters("Is");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t2");
						xmlWriter.writeCharacters("this");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t3");
						xmlWriter.writeCharacters("example");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t4");
						xmlWriter.writeCharacters("more");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t5");
						xmlWriter.writeCharacters("complicated");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t6");
						xmlWriter.writeCharacters("than");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t7");
						xmlWriter.writeCharacters("it");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t8");
						xmlWriter.writeCharacters("appears");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t9");
						xmlWriter.writeCharacters("to");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t10");
						xmlWriter.writeCharacters("be");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t11");
						xmlWriter.writeCharacters("?");
					xmlWriter.writeEndElement();
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_MORPHOLOGY, TCFDictionary.NS_VALUE_TC);					
					/* Is */
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ANALYSIS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t1");
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TAG, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_FS, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "cat");
								xmlWriter.writeCharacters("verb");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "person");
								xmlWriter.writeCharacters("3");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "number");
								xmlWriter.writeCharacters("singular");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "tense");
								xmlWriter.writeCharacters("present");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "indicative");
								xmlWriter.writeCharacters("true");
								xmlWriter.writeEndElement();
							xmlWriter.writeEndElement();
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SEGMENTATION, TCFDictionary.NS_VALUE_TC);						
							xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "verb");
							xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "stem");
								xmlWriter.writeCharacters("be");
							xmlWriter.writeEndElement();
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();
					/* this */
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ANALYSIS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t2");
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TAG, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_FS, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "cat");
								xmlWriter.writeCharacters("determiner");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "number");
								xmlWriter.writeCharacters("singular");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "definiteness");
								xmlWriter.writeCharacters("true");
								xmlWriter.writeEndElement();
							xmlWriter.writeEndElement();
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SEGMENTATION, TCFDictionary.NS_VALUE_TC);						
							xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "determiner");
							xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "stem");
								xmlWriter.writeCharacters("this");
							xmlWriter.writeEndElement();
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();
					/* example */
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ANALYSIS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t3");
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TAG, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_FS, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "cat");
								xmlWriter.writeCharacters("noun");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "number");
								xmlWriter.writeCharacters("singular");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "gender");
								xmlWriter.writeCharacters("neuter");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "case");
								xmlWriter.writeCharacters("nominative");
								xmlWriter.writeEndElement();
							xmlWriter.writeEndElement();
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SEGMENTATION, TCFDictionary.NS_VALUE_TC);						
							xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "noun");
							xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "stem");
								xmlWriter.writeCharacters("example");
							xmlWriter.writeEndElement();
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();
					/* more complicated */
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ANALYSIS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t4 t5");
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TAG, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_FS, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "cat");
								xmlWriter.writeCharacters("adjective");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "comparative");
								xmlWriter.writeCharacters("true");
								xmlWriter.writeEndElement();
							xmlWriter.writeEndElement();
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SEGMENTATION, TCFDictionary.NS_VALUE_TC);						
							xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "adjective");
							xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "stem");
								xmlWriter.writeCharacters("complicated");/* we do not consider the linguistic discussion on that */
							xmlWriter.writeEndElement();
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();
					/* than */
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ANALYSIS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t6");
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TAG, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_FS, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "cat");
								xmlWriter.writeCharacters("conjunction");
								xmlWriter.writeEndElement();
							xmlWriter.writeEndElement();
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SEGMENTATION, TCFDictionary.NS_VALUE_TC);						
							xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "conjunction");
							xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "stem");
								xmlWriter.writeCharacters("than");
							xmlWriter.writeEndElement();
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();
					/* it */
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ANALYSIS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t7");
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TAG, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_FS, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "cat");
								xmlWriter.writeCharacters("personal pronoun");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "person");
								xmlWriter.writeCharacters("3");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "number");
								xmlWriter.writeCharacters("singular");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "gender");
								xmlWriter.writeCharacters("neuter");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "case");
								xmlWriter.writeCharacters("nominative");
								xmlWriter.writeEndElement();
							xmlWriter.writeEndElement();
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SEGMENTATION, TCFDictionary.NS_VALUE_TC);						
							xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "personal pronoun");
							xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "stem");
								xmlWriter.writeCharacters("it");
							xmlWriter.writeEndElement();
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();
					/* appears */
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ANALYSIS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t8");
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TAG, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_FS, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "cat");
								xmlWriter.writeCharacters("verb");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "person");
								xmlWriter.writeCharacters("3");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "number");
								xmlWriter.writeCharacters("singular");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "tense");
								xmlWriter.writeCharacters("present");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "indicative");
								xmlWriter.writeCharacters("true");
								xmlWriter.writeEndElement();
							xmlWriter.writeEndElement();
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SEGMENTATION, TCFDictionary.NS_VALUE_TC);						
							xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "verb");
							xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "stem");
								xmlWriter.writeCharacters("appear");
							xmlWriter.writeEndElement();
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();
					/* to be */
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ANALYSIS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t9 t10");
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TAG, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_FS, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "cat");
								xmlWriter.writeCharacters("verb");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "infinitive");
								xmlWriter.writeCharacters("true");
								xmlWriter.writeEndElement();
							xmlWriter.writeEndElement();
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SEGMENTATION, TCFDictionary.NS_VALUE_TC);						
							xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "verb");
							xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "stem");
								xmlWriter.writeCharacters("be");
							xmlWriter.writeEndElement();
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();
					/* ? */
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ANALYSIS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t11");
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TAG, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_FS, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "cat");
								xmlWriter.writeCharacters("punctuation");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "punctuation");
								xmlWriter.writeCharacters("question");
								xmlWriter.writeEndElement();
							xmlWriter.writeEndElement();
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SEGMENTATION, TCFDictionary.NS_VALUE_TC);						
							xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "punctuation");
							xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "stem");
								xmlWriter.writeCharacters(".");
							xmlWriter.writeEndElement();
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();
				xmlWriter.writeEndElement();
			xmlWriter.writeEndElement();
		xmlWriter.writeEndDocument();
		
		/* generating salt sample */
		SDocument doc = SaltFactory.eINSTANCE.createSDocument();
		doc.setSDocumentGraph(SaltFactory.eINSTANCE.createSDocumentGraph());
		SaltSample.createPrimaryData(doc);
		SaltSample.createTokens2(doc);
		
		/* adding morphological annotation manually to salt sample */
		/* TODO add to salt sample (Florian okay)*/
		SDocumentGraph docGraph = doc.getSDocumentGraph();
		EList<SToken> docTokens = docGraph.getSortedSTokenByText();
		SLayer docMorphLayer = SaltFactory.eINSTANCE.createSLayer();
		docMorphLayer.setSName(TCFMapperImport.LAYER_TCF_MORPHOLOGY);
		EList<SNode> docMorph = docMorphLayer.getSNodes();

		SSpan sSpan = docGraph.createSSpan(docTokens.get(0));//Is
		sSpan.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "cat", "verb");
		sSpan.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "person", "3");
		sSpan.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "number", "singular");
		sSpan.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "tense", "present");
		sSpan.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "indicative", "true");
//		sSpan.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
		//in this last annotation (storage of segment.type) we use a namespace to avoid ambiguities
		//with potential morphological properties used in <analysis>...</analysis> (therefore namespace = TAG_TC_SEGMENT)
//		sSpan.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "be");
		//the character sequence between <segment>...</segment> will be represented in the same namespace and with key=TAG(=namespace)
		//I'm not sure I like that
		docMorph.add(sSpan);
		
		sSpan = docGraph.createSSpan(docTokens.get(1));//this
		sSpan.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "cat", "determiner");
		sSpan.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "number", "singular");
		sSpan.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "definiteness", "true");
//		sSpan.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
		docMorph.add(sSpan);
		
		sSpan = docGraph.createSSpan(docTokens.get(2));//example
		sSpan.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "cat", "noun");
		sSpan.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "number", "singular");
		sSpan.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "gender", "neuter");
		sSpan.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "case", "nominative");
//		sSpan.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
//		sSpan.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "example");
		docMorph.add(sSpan);
		
		EList<SToken> spanTokens = new BasicEList<SToken>();
		spanTokens.add(docTokens.get(3));
		spanTokens.add(docTokens.get(4));
		
		sSpan = docGraph.createSSpan(spanTokens);//more complicated		
		sSpan.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "cat", "adjective");
		sSpan.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "comparative", "true");
//		sSpan.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
//		sSpan.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "complicated");
		docMorph.add(sSpan);
		
		spanTokens.clear();
		
		sSpan = docGraph.createSSpan(docTokens.get(5));//than
		sSpan.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "cat", "conjunction");
		sSpan.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
//		sSpan.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "than");
		docMorph.add(sSpan);		
		
		sSpan = docGraph.createSSpan(docTokens.get(6));//it
		sSpan.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "cat", "personal pronoun");
		sSpan.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "number", "singular");
		sSpan.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "person", "3");
		sSpan.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "gender", "neuter");
		sSpan.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "case", "nominative");
//		sSpan.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
//		sSpan.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "it");
		docMorph.add(sSpan);
		
		sSpan = docGraph.createSSpan(docTokens.get(7));//appears
		sSpan.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "cat", "verb");
		sSpan.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "person", "3");
		sSpan.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "number", "singular");
		sSpan.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "tense", "present");
		sSpan.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "indicative", "true");
//		sSpan.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
//		sSpan.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "appear");
		docMorph.add(sSpan);
		
		spanTokens.add(docTokens.get(8));
		spanTokens.add(docTokens.get(9));
		
		sSpan = docGraph.createSSpan(spanTokens);//to be
		sSpan.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "cat", "verb");
		sSpan.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "infinitive", "true");
//		sSpan.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
//		sSpan.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "be");
		docMorph.add(sSpan);
		
		spanTokens.clear();
		
		sSpan = docGraph.createSSpan(docTokens.get(10));//?
		sSpan.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "cat", "punctuation");
		/*TODO*/
//		sSpan.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
//		sSpan.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, ".");
		docMorph.add(sSpan);
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_MORPHOLOGY);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		this.getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, false);
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, SPAN_REUSE);
		
		/* start mapper */
				
		this.getFixture().mapSDocument();
		
		
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = this.getFixture().getSDocument().getSDocumentGraph();
		assertNotNull(fixGraph.getSLayerByName(TCFMapperImport.LAYER_TCF_MORPHOLOGY));
		EList<SNode> fixMorph = fixGraph.getSLayerByName(TCFMapperImport.LAYER_TCF_MORPHOLOGY).get(0).getSNodes();	
		
		assertNotNull(fixMorph);
		assertNotEquals(fixMorph.size(), 0);
		assertEquals(docMorph.size(), fixMorph.size());
		assertNotEquals(fixMorph.size(), fixGraph.getSTokens().size());
				
		SNode docNode = null;
		SNode fixNode = null;		
		for(int i=0; i<docMorph.size(); i++){			
			docNode = docMorph.get(i);
			fixNode = fixMorph.get(i);
			/*TEST*//*TEST*//* fixNode of type SSpan? */
			assertTrue(fixNode instanceof SSpan);
			/* both overlap the same SText? */
			assertEquals(docGraph.getSText(docNode), fixGraph.getSText(fixNode));
			for(SAnnotation sAnno : docNode.getSAnnotations()){
				String qName = sAnno.getQName();
				/* compare annotations */
				if(DEBUG){
					}
				assertNotNull(fixNode.getSAnnotation(qName));				
				assertEquals(sAnno.getValue(), fixNode.getSAnnotation(qName).getValue());
			}
			/*TODO Segment as annotation of the annotation? Not implemented yet! (See issue #4) */
		}
		
	}
	
	/**This method tests if a valid TCF-XML-structure containing morphology
	 * annotations is converted to salt correctly by {@link TCFMapperImport}. The Mapper
	 * is supposed to build a span only for morphology annotations on multiple
	 * {@link SToken} objects. In case of single tokens the {@link SAnnotation} is build
	 * directly on the {@link SToken} object.
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testMorphologyShrinked() throws XMLStreamException, FileNotFoundException{		
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o= XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);
		
		xmlWriter.writeStartDocument();
		xmlWriter.writeProcessingInstruction(TCFDictionary.TCF_PI);
		xmlWriter.writeStartElement(TCFDictionary.NS_WL, TCFDictionary.TAG_WL_D_SPIN, TCFDictionary.NS_VALUE_WL);
		xmlWriter.writeNamespace(TCFDictionary.NS_WL, TCFDictionary.NS_VALUE_WL);
		xmlWriter.writeNamespace(TCFDictionary.NS_ED, TCFDictionary.NS_VALUE_ED);
		xmlWriter.writeNamespace(TCFDictionary.NS_LX, TCFDictionary.NS_VALUE_LX);
		xmlWriter.writeNamespace(TCFDictionary.NS_MD, TCFDictionary.NS_VALUE_MD);
		xmlWriter.writeNamespace(TCFDictionary.NS_TC, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_VERSION, "4.0");
			xmlWriter.writeStartElement(TCFDictionary.NS_MD, TCFDictionary.TAG_MD_METADATA, TCFDictionary.NS_VALUE_MD);
			xmlWriter.writeEndElement();
			xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTCORPUS, TCFDictionary.NS_VALUE_TC);
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXT, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeCharacters(EXAMPLE_TEXT);
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKENS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t1");
						xmlWriter.writeCharacters("Is");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t2");
						xmlWriter.writeCharacters("this");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t3");
						xmlWriter.writeCharacters("example");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t4");
						xmlWriter.writeCharacters("more");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t5");
						xmlWriter.writeCharacters("complicated");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t6");
						xmlWriter.writeCharacters("than");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t7");
						xmlWriter.writeCharacters("it");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t8");
						xmlWriter.writeCharacters("appears");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t9");
						xmlWriter.writeCharacters("to");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t10");
						xmlWriter.writeCharacters("be");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t11");
						xmlWriter.writeCharacters("?");
					xmlWriter.writeEndElement();
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_MORPHOLOGY, TCFDictionary.NS_VALUE_TC);					
					/* Is */
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ANALYSIS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t1");
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TAG, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_FS, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "cat");
								xmlWriter.writeCharacters("verb");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "person");
								xmlWriter.writeCharacters("3");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "number");
								xmlWriter.writeCharacters("singular");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "tense");
								xmlWriter.writeCharacters("present");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "indicative");
								xmlWriter.writeCharacters("true");
								xmlWriter.writeEndElement();
							xmlWriter.writeEndElement();
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SEGMENTATION, TCFDictionary.NS_VALUE_TC);						
							xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "verb");
							xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "stem");
								xmlWriter.writeCharacters("be");
							xmlWriter.writeEndElement();
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();
					/* this */
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ANALYSIS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t2");
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TAG, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_FS, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "cat");
								xmlWriter.writeCharacters("determiner");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "number");
								xmlWriter.writeCharacters("singular");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "definiteness");
								xmlWriter.writeCharacters("true");
								xmlWriter.writeEndElement();
							xmlWriter.writeEndElement();
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SEGMENTATION, TCFDictionary.NS_VALUE_TC);						
							xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "determiner");
							xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "stem");
								xmlWriter.writeCharacters("this");
							xmlWriter.writeEndElement();
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();
					/* example */
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ANALYSIS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t3");
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TAG, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_FS, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "cat");
								xmlWriter.writeCharacters("noun");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "number");
								xmlWriter.writeCharacters("singular");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "gender");
								xmlWriter.writeCharacters("neuter");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "case");
								xmlWriter.writeCharacters("nominative");
								xmlWriter.writeEndElement();
							xmlWriter.writeEndElement();
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SEGMENTATION, TCFDictionary.NS_VALUE_TC);						
							xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "noun");
							xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "stem");
								xmlWriter.writeCharacters("example");
							xmlWriter.writeEndElement();
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();
					/* more complicated */
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ANALYSIS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t4 t5");
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TAG, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_FS, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "cat");
								xmlWriter.writeCharacters("adjective");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "comparative");
								xmlWriter.writeCharacters("true");
								xmlWriter.writeEndElement();
							xmlWriter.writeEndElement();
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SEGMENTATION, TCFDictionary.NS_VALUE_TC);						
							xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "adjective");
							xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "stem");
								xmlWriter.writeCharacters("complicated");/* we do not consider the linguistic discussion on that */
							xmlWriter.writeEndElement();
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();
					/* than */
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ANALYSIS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t6");
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TAG, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_FS, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "cat");
								xmlWriter.writeCharacters("conjunction");
								xmlWriter.writeEndElement();
							xmlWriter.writeEndElement();
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SEGMENTATION, TCFDictionary.NS_VALUE_TC);						
							xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "conjunction");
							xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "stem");
								xmlWriter.writeCharacters("than");
							xmlWriter.writeEndElement();
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();
					/* it */
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ANALYSIS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t7");
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TAG, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_FS, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "cat");
								xmlWriter.writeCharacters("personal pronoun");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "person");
								xmlWriter.writeCharacters("3");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "number");
								xmlWriter.writeCharacters("singular");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "gender");
								xmlWriter.writeCharacters("neuter");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "case");
								xmlWriter.writeCharacters("nominative");
								xmlWriter.writeEndElement();
							xmlWriter.writeEndElement();
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SEGMENTATION, TCFDictionary.NS_VALUE_TC);						
							xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "personal pronoun");
							xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "stem");
								xmlWriter.writeCharacters("it");
							xmlWriter.writeEndElement();
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();
					/* appears */
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ANALYSIS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t8");
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TAG, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_FS, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "cat");
								xmlWriter.writeCharacters("verb");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "person");
								xmlWriter.writeCharacters("3");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "number");
								xmlWriter.writeCharacters("singular");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "tense");
								xmlWriter.writeCharacters("present");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "indicative");
								xmlWriter.writeCharacters("true");
								xmlWriter.writeEndElement();
							xmlWriter.writeEndElement();
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SEGMENTATION, TCFDictionary.NS_VALUE_TC);						
							xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "verb");
							xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "stem");
								xmlWriter.writeCharacters("appear");
							xmlWriter.writeEndElement();
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();
					/* to be */
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ANALYSIS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t9 t10");
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TAG, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_FS, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "cat");
								xmlWriter.writeCharacters("verb");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "infinitive");
								xmlWriter.writeCharacters("true");
								xmlWriter.writeEndElement();
							xmlWriter.writeEndElement();
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SEGMENTATION, TCFDictionary.NS_VALUE_TC);						
							xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "verb");
							xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "stem");
								xmlWriter.writeCharacters("be");
							xmlWriter.writeEndElement();
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();
					/* ? */
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ANALYSIS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t11");
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TAG, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_FS, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "cat");
								xmlWriter.writeCharacters("punctuation");
								xmlWriter.writeEndElement();
								xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_F, TCFDictionary.NS_VALUE_TC);
								xmlWriter.writeAttribute(TCFDictionary.ATT_NAME, "punctuation");
								xmlWriter.writeCharacters("question");
								xmlWriter.writeEndElement();
							xmlWriter.writeEndElement();
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SEGMENTATION, TCFDictionary.NS_VALUE_TC);						
							xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_CAT, "punctuation");
							xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "stem");
								xmlWriter.writeCharacters(".");
							xmlWriter.writeEndElement();
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();
				xmlWriter.writeEndElement();
			xmlWriter.writeEndElement();
		xmlWriter.writeEndDocument();
		
		/* generating salt sample */
		SDocument doc = SaltFactory.eINSTANCE.createSDocument();
		doc.setSDocumentGraph(SaltFactory.eINSTANCE.createSDocumentGraph());
		SaltSample.createPrimaryData(doc);
		SaltSample.createTokens2(doc);
		
		/* adding morphological annotation manually to salt sample */
		/* TODO add to salt sample (Florian okay)*/
		SDocumentGraph docGraph = doc.getSDocumentGraph();
		EList<SToken> docTokens = docGraph.getSortedSTokenByText();
		SLayer docMorphLayer = SaltFactory.eINSTANCE.createSLayer();
		docMorphLayer.setSName(TCFMapperImport.LAYER_TCF_MORPHOLOGY);
		EList<SNode> docMorph = docMorphLayer.getSNodes();
		
		SNode sNode = docTokens.get(0);
		
		sNode.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "cat", "verb");//Is
		sNode.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "person", "3");
		sNode.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "number", "singular");
		sNode.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "tense", "present");
		sNode.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "indicative", "true");
//		sNode.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
		//in this last annotation (storage of segment.type) we use a namespace to avoid ambiguities
		//with potential morphological properties used in <analysis>...</analysis> (therefore namespace = TAG_TC_SEGMENT)
//		sNode.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "be");
		//the character sequence between <segment>...</segment> will be represented in the same namespace and with key=TAG(=namespace)
		//I'm not sure I like that
		docMorph.add(sNode);
		
		sNode = docTokens.get(1);//this
		sNode.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "cat", "determiner");
		sNode.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "number", "singular");
		sNode.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "definiteness", "true");
//		sNode.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
		docMorph.add(sNode);
		
		sNode = docTokens.get(2);//example
		sNode.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "cat", "noun");
		sNode.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "number", "singular");
		sNode.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "gender", "neuter");
		sNode.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "case", "nominative");
//		sNode.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
//		sNode.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "example");
		docMorph.add(sNode);
		
		EList<SToken> spanTokens = new BasicEList<SToken>();
		spanTokens.add(docTokens.get(3));
		spanTokens.add(docTokens.get(4));
		
		sNode = docGraph.createSSpan(spanTokens);//more complicated		
		sNode.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "cat", "adjective");
		sNode.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "comparative", "true");
//		sNode.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
//		sNode.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "complicated");
		docMorph.add(sNode);
		
		spanTokens.clear();
		
		sNode = docTokens.get(5);//than
		sNode.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "cat", "conjunction");
//		sNode.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
//		sNode.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "than");
		docMorph.add(sNode);		
		
		sNode = docTokens.get(6);//it
		sNode.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "cat", "personal pronoun");
		sNode.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "number", "singular");
		sNode.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "person", "3");
		sNode.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "gender", "neuter");
		sNode.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "case", "nominative");
//		sNode.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
//		sNode.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "it");
		docMorph.add(sNode);
		
		sNode = docTokens.get(7);//appears
		sNode.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "cat", "verb");
		sNode.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "person", "3");
		sNode.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "number", "singular");
		sNode.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "tense", "present");
		sNode.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "indicative", "true");
//		sNode.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
//		sNode.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "appear");
		docMorph.add(sNode);
		
		spanTokens.add(docTokens.get(8));
		spanTokens.add(docTokens.get(9));
		
		sNode = docGraph.createSSpan(spanTokens);//to be
		sNode.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "cat", "verb");
		sNode.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "infinitive", "true");
//		sNode.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
//		sNode.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "be");
		docMorph.add(sNode);
		
		spanTokens.clear();
		
		sNode = docTokens.get(10);//?
		sNode.createSAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "cat", "punctuation");
//		sNode.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
//		sNode.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, ".");
		docMorph.add(sNode);
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_MORPHOLOGY);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		this.getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, true);
		
		/* start mapper */
				
		this.getFixture().mapSDocument();
		
		
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = this.getFixture().getSDocument().getSDocumentGraph();
		assertNotNull(fixGraph.getSLayerByName(TCFMapperImport.LAYER_TCF_MORPHOLOGY));
		EList<SNode> fixMorph = fixGraph.getSLayerByName(TCFMapperImport.LAYER_TCF_MORPHOLOGY).get(0).getSNodes();
		
		assertNotNull(fixMorph);
		assertNotEquals(fixMorph.size(), 0);
		assertEquals(docMorph.size(), fixMorph.size());
		assertNotEquals(fixMorph.size(), fixGraph.getSTokens().size());		
				
		SNode docNode = null;
		SNode fixNode = null;		
		for(int i=0; i<docMorph.size(); i++){			
			docNode = docMorph.get(i);
			fixNode = fixMorph.get(i);
			/*TEST*//*TEST*//* both of the same type? */
			assertEquals(docNode.getClass(), fixNode.getClass());
			assertTrue((fixNode instanceof SToken)||(fixNode instanceof SSpan));//necessary?
			assertEquals(docGraph.getSText(docNode), fixGraph.getSText(fixNode));
			for(SAnnotation sAnno : docNode.getSAnnotations()){
				String qName = sAnno.getQName();
				/* compare annotations */
				if(DEBUG){
					}
				assertNotNull(fixNode.getSAnnotation(qName));				
				assertEquals(sAnno.getValue(), fixNode.getSAnnotation(qName).getValue());
			}
			/*TODO Segment*/
		}
		
	}
		
	/**This method tests if a valid TCF-XML-structure containing named entity
	 * annotations is converted to salt correctly by {@link TCFMapperImport} 
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testNamedEntitiesShrinked() throws XMLStreamException, FileNotFoundException{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o= XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);
		
		xmlWriter.writeStartDocument();
		xmlWriter.writeProcessingInstruction(TCFDictionary.TCF_PI);
		xmlWriter.writeStartElement(TCFDictionary.NS_WL, TCFDictionary.TAG_WL_D_SPIN, TCFDictionary.NS_VALUE_WL);
		xmlWriter.writeNamespace(TCFDictionary.NS_WL, TCFDictionary.NS_VALUE_WL);
		xmlWriter.writeNamespace(TCFDictionary.NS_ED, TCFDictionary.NS_VALUE_ED);
		xmlWriter.writeNamespace(TCFDictionary.NS_LX, TCFDictionary.NS_VALUE_LX);
		xmlWriter.writeNamespace(TCFDictionary.NS_MD, TCFDictionary.NS_VALUE_MD);
		xmlWriter.writeNamespace(TCFDictionary.NS_TC, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_VERSION, "4.0");
			xmlWriter.writeStartElement(TCFDictionary.NS_MD, TCFDictionary.TAG_MD_METADATA, TCFDictionary.NS_VALUE_MD);
			xmlWriter.writeEndElement();
			xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTCORPUS, TCFDictionary.NS_VALUE_TC);
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXT, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeCharacters(EXAMPLE_TEXT_NAMED_ENTITIES);
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKENS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t1");
						xmlWriter.writeCharacters("Martin");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t2");
						xmlWriter.writeCharacters("loves");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t3");
						xmlWriter.writeCharacters("New");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t4");
						xmlWriter.writeCharacters("York");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t5");
						xmlWriter.writeCharacters(".");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t6");
						xmlWriter.writeCharacters("He");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t7");
						xmlWriter.writeCharacters("lives");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t8");
						xmlWriter.writeCharacters("in");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t9");
						xmlWriter.writeCharacters("Friedrichshain");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t10");
						xmlWriter.writeCharacters(".");
					xmlWriter.writeEndElement();					
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_NAMEDENTITIES, TCFDictionary.NS_VALUE_TC);
				xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "anySet");
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ENTITY, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "ne1");
					xmlWriter.writeAttribute(TCFDictionary.ATT_CLASS, "person");
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t1");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ENTITY, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "ne2");
					xmlWriter.writeAttribute(TCFDictionary.ATT_CLASS, "location");
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t3 t4");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ENTITY, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "ne3");
					xmlWriter.writeAttribute(TCFDictionary.ATT_CLASS, "location");
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t9");
					xmlWriter.writeEndElement();
				xmlWriter.writeEndElement();
			xmlWriter.writeEndElement();
		xmlWriter.writeEndElement();
		xmlWriter.writeEndDocument();
		
		/* generating salt sample */
		SDocument doc = SaltFactory.eINSTANCE.createSDocument();
		SDocumentGraph docGraph = SaltFactory.eINSTANCE.createSDocumentGraph();
		doc.setSDocumentGraph(docGraph);
		docGraph.createSTextualDS(EXAMPLE_TEXT_NAMED_ENTITIES);
		docGraph.tokenize();
		EList<SToken> docTokens = docGraph.getSTokens();
		SLayer docNELayer = SaltFactory.eINSTANCE.createSLayer();
		docNELayer.setSName(TCFMapperImport.LAYER_NE);
		docGraph.addSLayer(docNELayer);
		EList<SNode> docNENodes = docNELayer.getSNodes();
		
		//add tokens to NE-layer and annotate them as named entities; add tag set (type) information to layer
		docTokens.get(0).createSAnnotation(TCFMapperImport.LAYER_NE, TCFDictionary.ATT_CLASS, "person");		
		docNENodes.add(docTokens.get(0));
		
		SSpan newYork = docGraph.createSSpan(docTokens.get(2));
		docGraph.addSNode(newYork, docTokens.get(3), STYPE_NAME.SSPANNING_RELATION);
		newYork.createSAnnotation(TCFMapperImport.LAYER_NE, TCFDictionary.ATT_CLASS, "location");
		docNENodes.add(newYork);
		
		docTokens.get(8).createSAnnotation(TCFMapperImport.LAYER_NE, TCFDictionary.ATT_CLASS, "location");
		docNENodes.add(docTokens.get(8));
		
		docNELayer.createSMetaAnnotation(null, TCFDictionary.ATT_TYPE, "anySet");
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_NE);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		this.getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, true);
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, false);
		
		/* start mapper */
				
		this.getFixture().mapSDocument();
		
		
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = this.getFixture().getSDocument().getSDocumentGraph();
		assertFalse(fixGraph.getSLayerByName(TCFMapperImport.LAYER_NE).isEmpty());
		SLayer fixNELayer = fixGraph.getSLayerByName(TCFMapperImport.LAYER_NE).get(0);		
		assertNotNull(fixNELayer.getSMetaAnnotation(TCFDictionary.ATT_TYPE));
		EList<SNode> fixNENodes = fixNELayer.getSNodes();
		assertFalse(fixNENodes.isEmpty());
		assertEquals(docNENodes.size(), fixNENodes.size());
		SAnnotation fixNEAnno = null;
		SNode docNode = null;
		SNode fixNode = null;
		for(int i=0; i<docNENodes.size(); i++){
			docNode = docNENodes.get(i);
			fixNode = fixNENodes.get(i);
			assertEquals(docGraph.getSText(docNode), fixGraph.getSText(fixNode));
			fixNEAnno = fixNode.getSAnnotation(TCFMapperImport.LAYER_NE+"::"+TCFDictionary.ATT_CLASS);
			assertNotNull(fixNEAnno);
			assertEquals(docNode.getSAnnotation(TCFMapperImport.LAYER_NE+"::"+TCFDictionary.ATT_CLASS).getSValue(), fixNEAnno.getSValue());
			if(DEBUG){}
			assertTrue(fixNode instanceof SSpan | fixNode instanceof SToken);
			assertEquals(docNode.getClass(), fixNode.getClass());
		}
	}
	
	/**This method tests if a valid TCF-XML-structure containing named entity
	 * annotations is converted to salt correctly by {@link TCFMapperImport} 
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testNamedEntitiesNotShrinked() throws XMLStreamException, FileNotFoundException{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o= XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);
		
		xmlWriter.writeStartDocument();
		xmlWriter.writeProcessingInstruction(TCFDictionary.TCF_PI);
		xmlWriter.writeStartElement(TCFDictionary.NS_WL, TCFDictionary.TAG_WL_D_SPIN, TCFDictionary.NS_VALUE_WL);
		xmlWriter.writeNamespace(TCFDictionary.NS_WL, TCFDictionary.NS_VALUE_WL);
		xmlWriter.writeNamespace(TCFDictionary.NS_ED, TCFDictionary.NS_VALUE_ED);
		xmlWriter.writeNamespace(TCFDictionary.NS_LX, TCFDictionary.NS_VALUE_LX);
		xmlWriter.writeNamespace(TCFDictionary.NS_MD, TCFDictionary.NS_VALUE_MD);
		xmlWriter.writeNamespace(TCFDictionary.NS_TC, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_VERSION, "4.0");
			xmlWriter.writeStartElement(TCFDictionary.NS_MD, TCFDictionary.TAG_MD_METADATA, TCFDictionary.NS_VALUE_MD);
			xmlWriter.writeEndElement();
			xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTCORPUS, TCFDictionary.NS_VALUE_TC);
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXT, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeCharacters(EXAMPLE_TEXT_NAMED_ENTITIES);
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKENS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t1");
						xmlWriter.writeCharacters("Martin");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t2");
						xmlWriter.writeCharacters("loves");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t3");
						xmlWriter.writeCharacters("New");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t4");
						xmlWriter.writeCharacters("York");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t5");
						xmlWriter.writeCharacters(".");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t6");
						xmlWriter.writeCharacters("He");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t7");
						xmlWriter.writeCharacters("lives");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t8");
						xmlWriter.writeCharacters("in");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t9");
						xmlWriter.writeCharacters("Friedrichshain");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t10");
						xmlWriter.writeCharacters(".");
					xmlWriter.writeEndElement();					
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_NAMEDENTITIES, TCFDictionary.NS_VALUE_TC);
				xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "anySet");
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ENTITY, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "ne1");
					xmlWriter.writeAttribute(TCFDictionary.ATT_CLASS, "person");
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t1");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ENTITY, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "ne2");
					xmlWriter.writeAttribute(TCFDictionary.ATT_CLASS, "location");
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t3 t4");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ENTITY, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "ne3");
					xmlWriter.writeAttribute(TCFDictionary.ATT_CLASS, "location");
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t9");
					xmlWriter.writeEndElement();
				xmlWriter.writeEndElement();
			xmlWriter.writeEndElement();
		xmlWriter.writeEndElement();
		xmlWriter.writeEndDocument();
		
		/* generating salt sample */
		SDocument doc = SaltFactory.eINSTANCE.createSDocument();
		SDocumentGraph docGraph = SaltFactory.eINSTANCE.createSDocumentGraph();
		doc.setSDocumentGraph(docGraph);
		docGraph.createSTextualDS(EXAMPLE_TEXT_NAMED_ENTITIES);
		docGraph.tokenize();
		EList<SToken> docTokens = docGraph.getSTokens();
		SLayer docNELayer = SaltFactory.eINSTANCE.createSLayer();
		docNELayer.setSName(TCFMapperImport.LAYER_NE);
		docGraph.addSLayer(docNELayer);
		EList<SNode> docNENodes = docNELayer.getSNodes();
		
		//add tokens to NE-layer and annotate them as named entities; add tag set (type) information to layer
		SSpan ne = docGraph.createSSpan(docTokens.get(0));
		ne.createSAnnotation(TCFMapperImport.LAYER_NE, TCFDictionary.ATT_CLASS, "person");		
		docNENodes.add(ne);
		
		ne = docGraph.createSSpan(docTokens.get(2));
		docGraph.addSNode(ne, docTokens.get(3), STYPE_NAME.SSPANNING_RELATION);
		ne.createSAnnotation(TCFMapperImport.LAYER_NE, TCFDictionary.ATT_CLASS, "location");
		docNENodes.add(ne);
		
		ne = docGraph.createSSpan(docTokens.get(8));
		ne.createSAnnotation(TCFMapperImport.LAYER_NE, TCFDictionary.ATT_CLASS, "location");
		docNENodes.add(ne);
		
		docNELayer.createSMetaAnnotation(null, TCFDictionary.ATT_TYPE, "anySet");
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_NE);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		this.getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, false);
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, false);
		
		/* start mapper */
				
		this.getFixture().mapSDocument();
		
		
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = this.getFixture().getSDocument().getSDocumentGraph();
		assertFalse(fixGraph.getSLayerByName(TCFMapperImport.LAYER_NE).isEmpty());
		SLayer fixNELayer = fixGraph.getSLayerByName(TCFMapperImport.LAYER_NE).get(0);
		assertNotNull(fixNELayer.getSMetaAnnotation(TCFDictionary.ATT_TYPE));
		assertEquals(docNELayer.getSMetaAnnotation(TCFDictionary.ATT_TYPE), fixNELayer.getSMetaAnnotation(TCFDictionary.ATT_TYPE));
		EList<SNode> fixNENodes = fixNELayer.getSNodes();
		assertFalse(fixNENodes.isEmpty());
		assertEquals(docNENodes.size(), fixNENodes.size());
		SAnnotation fixNEAnno = null;
		SNode docNode = null;
		SNode fixNode = null;
		for(int i=0; i<docNENodes.size(); i++){
			docNode = docNENodes.get(i);
			fixNode = fixNENodes.get(i);
			assertEquals(docGraph.getSText(docNode), fixGraph.getSText(fixNode));
			fixNEAnno = fixNode.getSAnnotation(TCFMapperImport.LAYER_NE+"::"+TCFDictionary.ATT_CLASS);
			assertNotNull(fixNEAnno);
			assertEquals(docNode.getSAnnotation(TCFMapperImport.LAYER_NE+"::"+TCFDictionary.ATT_CLASS).getSValue(), fixNEAnno.getSValue());			
			assertTrue(fixNode instanceof SSpan);			
		}
	}
	
	/**This method tests if a valid TCF-XML-structure containing reference
	 * annotations is converted to salt correctly by {@link TCFMapperImport} 
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testReferencesShrinked() throws XMLStreamException, FileNotFoundException{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o= XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);
		
		xmlWriter.writeStartDocument();
		xmlWriter.writeProcessingInstruction(TCFDictionary.TCF_PI);
		xmlWriter.writeStartElement(TCFDictionary.NS_WL, TCFDictionary.TAG_WL_D_SPIN, TCFDictionary.NS_VALUE_WL);
		xmlWriter.writeNamespace(TCFDictionary.NS_WL, TCFDictionary.NS_VALUE_WL);
		xmlWriter.writeNamespace(TCFDictionary.NS_ED, TCFDictionary.NS_VALUE_ED);
		xmlWriter.writeNamespace(TCFDictionary.NS_LX, TCFDictionary.NS_VALUE_LX);
		xmlWriter.writeNamespace(TCFDictionary.NS_MD, TCFDictionary.NS_VALUE_MD);
		xmlWriter.writeNamespace(TCFDictionary.NS_TC, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_VERSION, "4.0");
			xmlWriter.writeStartElement(TCFDictionary.NS_MD, TCFDictionary.TAG_MD_METADATA, TCFDictionary.NS_VALUE_MD);
			xmlWriter.writeEndElement();
			xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTCORPUS, TCFDictionary.NS_VALUE_TC);
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXT, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeCharacters(EXAMPLE_TEXT_REFERENCE);
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKENS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t1");
						xmlWriter.writeCharacters("I");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t2");
						xmlWriter.writeCharacters("love");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t3");
						xmlWriter.writeCharacters("New");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t4");
						xmlWriter.writeCharacters("York");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t5");
						xmlWriter.writeCharacters(".");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t6");
						xmlWriter.writeCharacters("It");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t7");
						xmlWriter.writeCharacters("is");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t8");
						xmlWriter.writeCharacters("the");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t9");
						xmlWriter.writeCharacters("most");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t10");
						xmlWriter.writeCharacters("beautiful");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t11");
						xmlWriter.writeCharacters("place");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t12");
						xmlWriter.writeCharacters(".");
					xmlWriter.writeEndElement();
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_REFERENCES, TCFDictionary.NS_VALUE_TC);
				xmlWriter.writeAttribute(TCFDictionary.ATT_TYPETAGSET, "unknown");
				xmlWriter.writeAttribute(TCFDictionary.ATT_RELTAGSET, "unknown");					
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ENTITY, TCFDictionary.NS_VALUE_TC);//entity "New York"
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_REFERENCE, TCFDictionary.NS_VALUE_TC);//NY-1
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "rc1");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t3 t4");
						xmlWriter.writeAttribute(TCFDictionary.ATT_MINTOKIDS, "t3 t4");//no clearly identified head
						xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "name");
						xmlWriter.writeEndElement();//End of NY-1
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_REFERENCE, TCFDictionary.NS_VALUE_TC);//NY-2
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "rc2");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t6");
						xmlWriter.writeAttribute(TCFDictionary.ATT_MINTOKIDS, "t6");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "pronoun");
						xmlWriter.writeAttribute(TCFDictionary.ATT_REL, "anaphoric");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TARGET, "rc1");
						xmlWriter.writeEndElement();//End of NY-2
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_REFERENCE, TCFDictionary.NS_VALUE_TC);//NY-2
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "rc3");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t8 t9 t10 t11");
						xmlWriter.writeAttribute(TCFDictionary.ATT_MINTOKIDS, "t11");//we choose the nominal head, not the determiner
						xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "noun");
						xmlWriter.writeAttribute(TCFDictionary.ATT_REL, "non-anaphoric");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TARGET, "rc1");
						xmlWriter.writeEndElement();//End of NY-3
					xmlWriter.writeEndElement();//End of entity "New York"
				xmlWriter.writeEndElement();
			xmlWriter.writeEndElement();
		xmlWriter.writeEndElement();
		xmlWriter.writeEndDocument();
	
		/* generating salt sample */
		SDocument doc = SaltFactory.eINSTANCE.createSDocument();
		SDocumentGraph docGraph = SaltFactory.eINSTANCE.createSDocumentGraph();
		doc.setSDocumentGraph(docGraph);
		docGraph.createSTextualDS(EXAMPLE_TEXT_REFERENCE);
		docGraph.tokenize();
		EList<SToken> docTokens = docGraph.getSTokens();
		
		//head marker:
		SAnnotation refHead = SaltFactory.eINSTANCE.createSAnnotation();
		refHead.setName(TCFMapperImport.HEAD_MARKER);
		refHead.setValue(TCFMapperImport.HEAD_MARKER);
		
		//reference layer:
		SLayer docRefLayer = SaltFactory.eINSTANCE.createSLayer();
		docRefLayer.setSName(TCFMapperImport.LAYER_REFERENCES);
		docRefLayer.createSMetaAnnotation(null, TCFDictionary.ATT_TYPETAGSET, "unknown");
		docRefLayer.createSMetaAnnotation(null, TCFDictionary.ATT_RELTAGSET, "unknown");
		
		//relation:
		SPointingRelation reference = SaltFactory.eINSTANCE.createSPointingRelation();
		
		//entity "New York":
		///"New York"
		SSpan newYork = docGraph.createSSpan(docTokens.get(2));
		docGraph.addSNode(newYork, docTokens.get(3), STYPE_NAME.SSPANNING_RELATION);
		newYork.createSAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_TYPE, "name");
		docTokens.get(2).addSAnnotation(refHead);
		docTokens.get(3).addSAnnotation(refHead);
		docRefLayer.getSNodes().add(newYork);		
		///"the most beautiful place"
		SSpan theMostBeautifulPlace = docGraph.createSSpan(docTokens.get(7));
		docGraph.addSNode(theMostBeautifulPlace, docTokens.get(8), STYPE_NAME.SSPANNING_RELATION);
		docGraph.addSNode(theMostBeautifulPlace, docTokens.get(9), STYPE_NAME.SSPANNING_RELATION);
		docGraph.addSNode(theMostBeautifulPlace, docTokens.get(10), STYPE_NAME.SSPANNING_RELATION);		
		theMostBeautifulPlace.createSAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_TYPE, "noun");		
		docTokens.get(10).addSAnnotation(refHead);		
		reference.setSSource(theMostBeautifulPlace);
		reference.setSTarget(newYork);
		reference.createSAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_REL, "non-anaphoric");
		docRefLayer.getSNodes().add(theMostBeautifulPlace);
		docRefLayer.getSRelations().add(reference);
		///"it"
		docTokens.get(5).createSAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_TYPE, "pronoun");		
		docTokens.get(5).addSAnnotation(refHead);
		reference = SaltFactory.eINSTANCE.createSPointingRelation();
		reference.setSSource(docTokens.get(5));
		reference.setSTarget(newYork);
		reference.createSAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_REL, "anaphoric");
		docRefLayer.getSNodes().add(docTokens.get(5));
		docRefLayer.getSRelations().add(reference);
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_REFERENCES);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		this.getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, true);
		
		/* start mapper */
				
		this.getFixture().mapSDocument();
		
		/* compare template salt model to imported salt model */		
		SDocumentGraph fixGraph = this.getFixture().getSDocument().getSDocumentGraph();
		assertNotNull(fixGraph.getSLayerByName(TCFMapperImport.LAYER_REFERENCES));
		SLayer fixRefLayer = fixGraph.getSLayerByName(TCFMapperImport.LAYER_REFERENCES).get(0);
		assertFalse(fixRefLayer.getSNodes().isEmpty());
		assertFalse(fixRefLayer.getSRelations().isEmpty());		
		assertEquals(docRefLayer.getSNodes().size(), fixRefLayer.getSNodes().size());
		EList<SRelation> docReferences = docRefLayer.getSRelations();
		EList<SRelation> fixReferences = fixRefLayer.getSRelations();
		assertEquals(docReferences.size(), fixReferences.size());
		assertNotNull(fixRefLayer.getSMetaAnnotation(TCFDictionary.ATT_TYPETAGSET));
		assertNotNull(fixRefLayer.getSMetaAnnotation(TCFDictionary.ATT_RELTAGSET));
		assertEquals(docRefLayer.getSAnnotation(TCFDictionary.ATT_TYPETAGSET), fixRefLayer.getSAnnotation(TCFDictionary.ATT_TYPETAGSET));
		assertEquals(docRefLayer.getSAnnotation(TCFDictionary.ATT_RELTAGSET), fixRefLayer.getSAnnotation(TCFDictionary.ATT_RELTAGSET));
		
		SRelation docRef = null;
		SRelation fixRef = null;
		for(int i=0; i<docReferences.size(); i++){
			docRef = docReferences.get(i);
			fixRef = fixReferences.get(i);
			if(DEBUG){
				}
			/* compare source */
			assertNotNull(fixRef.getSSource());
			assertEquals(docGraph.getSText(docRef.getSSource()), fixGraph.getSText(fixRef.getSSource()));
			assertNotNull(fixRef.getSSource().getSAnnotation(TCFMapperImport.LAYER_REFERENCES+"::"+TCFDictionary.ATT_TYPE));
			assertEquals(docRef.getSSource().getSAnnotation(TCFMapperImport.LAYER_REFERENCES+"::"+TCFDictionary.ATT_TYPE), fixRef.getSSource().getSAnnotation(TCFMapperImport.LAYER_REFERENCES+"::"+TCFDictionary.ATT_TYPE));
			assertEquals(docRef.getSSource().getClass(), fixRef.getSSource().getClass());
			/* compare relation */
			assertNotNull(fixRef.getSAnnotation(TCFMapperImport.LAYER_REFERENCES+"::"+TCFDictionary.ATT_REL));
			/* compare target */
			assertNotNull(fixRef.getSTarget());
			assertEquals(docGraph.getSText(docRef.getSTarget()), fixGraph.getSText(fixRef.getSTarget()));
			assertNotNull(fixRef.getSTarget().getSAnnotation(TCFMapperImport.LAYER_REFERENCES+"::"+TCFDictionary.ATT_TYPE));
			assertEquals(docRef.getSTarget().getSAnnotation(TCFMapperImport.LAYER_REFERENCES+"::"+TCFDictionary.ATT_TYPE).getValue(), fixRef.getSTarget().getSAnnotation(TCFMapperImport.LAYER_REFERENCES+"::"+TCFDictionary.ATT_TYPE).getValue());
			assertEquals(docRef.getSTarget().getClass(), fixRef.getSTarget().getClass());
		}
		/*TODO compare head markings*/
	}
	
	/**This method tests if a valid TCF-XML-structure containing reference
	 * annotations is converted to salt correctly by {@link TCFMapperImport} 
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testReferencesShrinked2() throws XMLStreamException, FileNotFoundException{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o= XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);
		
		xmlWriter.writeStartDocument();
		xmlWriter.writeProcessingInstruction(TCFDictionary.TCF_PI);
		xmlWriter.writeStartElement(TCFDictionary.NS_WL, TCFDictionary.TAG_WL_D_SPIN, TCFDictionary.NS_VALUE_WL);
		xmlWriter.writeNamespace(TCFDictionary.NS_WL, TCFDictionary.NS_VALUE_WL);
		xmlWriter.writeNamespace(TCFDictionary.NS_ED, TCFDictionary.NS_VALUE_ED);
		xmlWriter.writeNamespace(TCFDictionary.NS_LX, TCFDictionary.NS_VALUE_LX);
		xmlWriter.writeNamespace(TCFDictionary.NS_MD, TCFDictionary.NS_VALUE_MD);
		xmlWriter.writeNamespace(TCFDictionary.NS_TC, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_VERSION, "4.0");
			xmlWriter.writeStartElement(TCFDictionary.NS_MD, TCFDictionary.TAG_MD_METADATA, TCFDictionary.NS_VALUE_MD);
			xmlWriter.writeEndElement();
			xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTCORPUS, TCFDictionary.NS_VALUE_TC);
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXT, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeCharacters(EXAMPLE_TEXT_REFERENCE);
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKENS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t1");
						xmlWriter.writeCharacters("I");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t2");
						xmlWriter.writeCharacters("love");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t3");
						xmlWriter.writeCharacters("New");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t4");
						xmlWriter.writeCharacters("York");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t5");
						xmlWriter.writeCharacters(".");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t6");
						xmlWriter.writeCharacters("It");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t7");
						xmlWriter.writeCharacters("is");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t8");
						xmlWriter.writeCharacters("the");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t9");
						xmlWriter.writeCharacters("most");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t10");
						xmlWriter.writeCharacters("beautiful");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t11");
						xmlWriter.writeCharacters("place");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t12");
						xmlWriter.writeCharacters(".");
					xmlWriter.writeEndElement();
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_REFERENCES, TCFDictionary.NS_VALUE_TC);
				xmlWriter.writeAttribute(TCFDictionary.ATT_TYPETAGSET, "unknown");
				xmlWriter.writeAttribute(TCFDictionary.ATT_RELTAGSET, "unknown");					
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ENTITY, TCFDictionary.NS_VALUE_TC);//entity "New York"						
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_REFERENCE, TCFDictionary.NS_VALUE_TC);//NY-2
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "rc2");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t6");
						xmlWriter.writeAttribute(TCFDictionary.ATT_MINTOKIDS, "t6");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "pronoun");
						xmlWriter.writeAttribute(TCFDictionary.ATT_REL, "anaphoric");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TARGET, "rc1");
						xmlWriter.writeEndElement();//End of NY-2
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_REFERENCE, TCFDictionary.NS_VALUE_TC);//NY-2
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "rc3");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t8 t9 t10 t11");
						xmlWriter.writeAttribute(TCFDictionary.ATT_MINTOKIDS, "t11");//we choose the nominal head, not the determiner
						xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "noun");
						xmlWriter.writeAttribute(TCFDictionary.ATT_REL, "non-anaphoric");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TARGET, "rc1");
						xmlWriter.writeEndElement();//End of NY-3
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_REFERENCE, TCFDictionary.NS_VALUE_TC);//NY-1
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "rc1");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t3 t4");
						xmlWriter.writeAttribute(TCFDictionary.ATT_MINTOKIDS, "t3 t4");//no clearly identified head
						xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "name");
						xmlWriter.writeEndElement();//End of NY-1
					xmlWriter.writeEndElement();//End of entity "New York"
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ENTITY, TCFDictionary.NS_VALUE_TC);//start pseudo entity						
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_REFERENCE, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "rc4");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t2");
						xmlWriter.writeAttribute(TCFDictionary.ATT_MINTOKIDS, "");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "test");
						xmlWriter.writeAttribute(TCFDictionary.ATT_REL, "non-anaphoric");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TARGET, "rc5");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_REFERENCE, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "rc5");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t7");
						xmlWriter.writeAttribute(TCFDictionary.ATT_MINTOKIDS, "");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "test");
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();//end of pseudo entity
				xmlWriter.writeEndElement();
			xmlWriter.writeEndElement();
		xmlWriter.writeEndElement();
		xmlWriter.writeEndDocument();
	
		/* generating salt sample */
		SDocument doc = SaltFactory.eINSTANCE.createSDocument();
		SDocumentGraph docGraph = SaltFactory.eINSTANCE.createSDocumentGraph();
		doc.setSDocumentGraph(docGraph);
		docGraph.createSTextualDS(EXAMPLE_TEXT_REFERENCE);
		docGraph.tokenize();
		EList<SToken> docTokens = docGraph.getSTokens();
		
		//head marker:
		SAnnotation refHead = SaltFactory.eINSTANCE.createSAnnotation();
		refHead.setName(TCFMapperImport.HEAD_MARKER);
		refHead.setValue(TCFMapperImport.HEAD_MARKER);
		
		//reference layer:
		SLayer docRefLayer = SaltFactory.eINSTANCE.createSLayer();
		docRefLayer.setSName(TCFMapperImport.LAYER_REFERENCES);
		docRefLayer.createSMetaAnnotation(null, TCFDictionary.ATT_TYPETAGSET, "unknown");
		docRefLayer.createSMetaAnnotation(null, TCFDictionary.ATT_RELTAGSET, "unknown");
		
		//relation:
		SPointingRelation reference = SaltFactory.eINSTANCE.createSPointingRelation();
		
		//entity "New York":
		///"New York"
		SSpan newYork = docGraph.createSSpan(docTokens.get(2));
		docGraph.addSNode(newYork, docTokens.get(3), STYPE_NAME.SSPANNING_RELATION);
		newYork.createSAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_TYPE, "name");
		docTokens.get(2).addSAnnotation(refHead);
		docTokens.get(3).addSAnnotation(refHead);
		docRefLayer.getSNodes().add(newYork);		
		///"the most beautiful place"
		SSpan theMostBeautifulPlace = docGraph.createSSpan(docTokens.get(7));
		docGraph.addSNode(theMostBeautifulPlace, docTokens.get(8), STYPE_NAME.SSPANNING_RELATION);
		docGraph.addSNode(theMostBeautifulPlace, docTokens.get(9), STYPE_NAME.SSPANNING_RELATION);
		docGraph.addSNode(theMostBeautifulPlace, docTokens.get(10), STYPE_NAME.SSPANNING_RELATION);		
		theMostBeautifulPlace.createSAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_TYPE, "noun");		
		docTokens.get(10).addSAnnotation(refHead);
		reference = (SPointingRelation)docGraph.addSNode(theMostBeautifulPlace, newYork, STYPE_NAME.SPOINTING_RELATION);
		reference.createSAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_REL, "non-anaphoric");
		docRefLayer.getSNodes().add(theMostBeautifulPlace);
		docRefLayer.getSRelations().add(reference);
		///"it"
		docTokens.get(5).createSAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_TYPE, "pronoun");		
		docTokens.get(5).addSAnnotation(refHead);
		reference = (SPointingRelation)docGraph.addSNode(docTokens.get(5), newYork, STYPE_NAME.SPOINTING_RELATION);
		reference.createSAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_REL, "anaphoric");
		docRefLayer.getSNodes().add(docTokens.get(5));
		docRefLayer.getSRelations().add(reference);
		
		//test entity
		///love
		docTokens.get(1).createSAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_TYPE, "test");
		docRefLayer.getSNodes().add(docTokens.get(1));
		///is
		docTokens.get(6).createSAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_TYPE, "test");
		docRefLayer.getSNodes().add(docTokens.get(6));
		///relation:
		reference = (SPointingRelation)docGraph.addSNode(docTokens.get(1), docTokens.get(6), STYPE_NAME.SPOINTING_RELATION);
		reference.createSAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_REL, "non-anaphoric");
		docRefLayer.getSRelations().add(reference);
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_REFERENCES);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		this.getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, true);
		
		/* start mapper */
				
		this.getFixture().mapSDocument();
		
		/* compare template salt model to imported salt model */		
		SDocumentGraph fixGraph = this.getFixture().getSDocument().getSDocumentGraph();
		assertNotNull(fixGraph.getSLayerByName(TCFMapperImport.LAYER_REFERENCES));
		SLayer fixRefLayer = fixGraph.getSLayerByName(TCFMapperImport.LAYER_REFERENCES).get(0);
		assertFalse(fixRefLayer.getSNodes().isEmpty());
		assertFalse(fixRefLayer.getSRelations().isEmpty());		
		assertEquals(docRefLayer.getSNodes().size(), fixRefLayer.getSNodes().size());
		EList<SRelation> docReferences = docRefLayer.getSRelations();
		EList<SRelation> fixReferences = fixRefLayer.getSRelations();
		assertEquals(docReferences.size(), fixReferences.size());
		assertNotNull(fixRefLayer.getSMetaAnnotation(TCFDictionary.ATT_TYPETAGSET));
		assertNotNull(fixRefLayer.getSMetaAnnotation(TCFDictionary.ATT_RELTAGSET));
		assertEquals(docRefLayer.getSAnnotation(TCFDictionary.ATT_TYPETAGSET), fixRefLayer.getSAnnotation(TCFDictionary.ATT_TYPETAGSET));
		assertEquals(docRefLayer.getSAnnotation(TCFDictionary.ATT_RELTAGSET), fixRefLayer.getSAnnotation(TCFDictionary.ATT_RELTAGSET));
		
		SRelation docRef = null;
		SRelation fixRef = null;		
		for(int i=0; i<docReferences.size(); i++){
			docRef = docReferences.get(i);
			fixRef = fixReferences.get(i);		
			/* compare source */
			assertNotNull(fixRef.getSSource());
			assertEquals(docGraph.getSText(docRef.getSSource()), fixGraph.getSText(fixRef.getSSource()));
			assertNotNull(fixRef.getSSource().getSAnnotation(TCFMapperImport.LAYER_REFERENCES+"::"+TCFDictionary.ATT_TYPE));
			assertEquals(docRef.getSSource().getSAnnotation(TCFMapperImport.LAYER_REFERENCES+"::"+TCFDictionary.ATT_TYPE), fixRef.getSSource().getSAnnotation(TCFMapperImport.LAYER_REFERENCES+"::"+TCFDictionary.ATT_TYPE));
			assertEquals(docRef.getSSource().getClass(), fixRef.getSSource().getClass());
			/* compare relation */
			assertNotNull(fixRef.getSAnnotation(TCFMapperImport.LAYER_REFERENCES+"::"+TCFDictionary.ATT_REL));
			if(DEBUG){
				}
			/* compare target */
			assertNotNull(fixRef.getSTarget());
			assertEquals(docGraph.getSText(docRef.getSTarget()), fixGraph.getSText(fixRef.getSTarget()));
			assertNotNull(fixRef.getSTarget().getSAnnotation(TCFMapperImport.LAYER_REFERENCES+"::"+TCFDictionary.ATT_TYPE));
			assertEquals(docRef.getSTarget().getSAnnotation(TCFMapperImport.LAYER_REFERENCES+"::"+TCFDictionary.ATT_TYPE).getValue(), fixRef.getSTarget().getSAnnotation(TCFMapperImport.LAYER_REFERENCES+"::"+TCFDictionary.ATT_TYPE).getValue());
			assertEquals(docRef.getSTarget().getClass(), fixRef.getSTarget().getClass());
		}
		
		StringBuilder tree = new StringBuilder();
		int j;
		for(SNode sNode : fixGraph.getSNodes()){
			tree.append(fixGraph.getSText(sNode));
			tree.append("--");
			j=0;
			if(sNode.getOutgoingSRelations().size()>0){
				while(!(sNode.getOutgoingSRelations().get(j) instanceof SPointingRelation)){
					j++;
					if(j==sNode.getOutgoingSRelations().size()){break;}				
				}
				if(j<sNode.getOutgoingSRelations().size()){
					tree.append(sNode.getOutgoingSRelations().get(j).getSAnnotation(TCFMapperImport.LAYER_REFERENCES+"::"+TCFDictionary.ATT_REL).getValue());
					tree.append("->");
					tree.append(fixGraph.getSText((SNode)sNode.getOutgoingSRelations().get(j).getTarget()));
													
				}
			}
			tree.delete(0, tree.length());
		}
		//doc
		for(SNode sNode : docGraph.getSNodes()){
			tree.append(docGraph.getSText(sNode));
			tree.append("--");
			j=0;
			if(sNode.getOutgoingSRelations().size()>0){
				while(!(sNode.getOutgoingSRelations().get(j) instanceof SPointingRelation)){
					j++;
					if(j==sNode.getOutgoingSRelations().size()){break;}				
				}
				if(j<sNode.getOutgoingSRelations().size()){
					tree.append(sNode.getOutgoingSRelations().get(j).getSAnnotation(TCFMapperImport.LAYER_REFERENCES+"::"+TCFDictionary.ATT_REL).getValue());
					tree.append("->");
					tree.append(docGraph.getSText((SNode)sNode.getOutgoingSRelations().get(j).getTarget()));
													
				}
			}
			tree.delete(0, tree.length());
		}
	}
	
	/**This method tests if a valid TCF-XML-structure containing reference
	 * annotations is converted to salt correctly by {@link TCFMapperImport} 
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testReferencesNotShrinked() throws XMLStreamException, FileNotFoundException{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o= XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);
		
		xmlWriter.writeStartDocument();
		xmlWriter.writeProcessingInstruction(TCFDictionary.TCF_PI);
		xmlWriter.writeStartElement(TCFDictionary.NS_WL, TCFDictionary.TAG_WL_D_SPIN, TCFDictionary.NS_VALUE_WL);
		xmlWriter.writeNamespace(TCFDictionary.NS_WL, TCFDictionary.NS_VALUE_WL);
		xmlWriter.writeNamespace(TCFDictionary.NS_ED, TCFDictionary.NS_VALUE_ED);
		xmlWriter.writeNamespace(TCFDictionary.NS_LX, TCFDictionary.NS_VALUE_LX);
		xmlWriter.writeNamespace(TCFDictionary.NS_MD, TCFDictionary.NS_VALUE_MD);
		xmlWriter.writeNamespace(TCFDictionary.NS_TC, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_VERSION, "4.0");
			xmlWriter.writeStartElement(TCFDictionary.NS_MD, TCFDictionary.TAG_MD_METADATA, TCFDictionary.NS_VALUE_MD);
			xmlWriter.writeEndElement();
			xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTCORPUS, TCFDictionary.NS_VALUE_TC);
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXT, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeCharacters(EXAMPLE_TEXT_REFERENCE);
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKENS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t1");
						xmlWriter.writeCharacters("I");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t2");
						xmlWriter.writeCharacters("love");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t3");
						xmlWriter.writeCharacters("New");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t4");
						xmlWriter.writeCharacters("York");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t5");
						xmlWriter.writeCharacters(".");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t6");
						xmlWriter.writeCharacters("It");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t7");
						xmlWriter.writeCharacters("is");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t8");
						xmlWriter.writeCharacters("the");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t9");
						xmlWriter.writeCharacters("most");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t10");
						xmlWriter.writeCharacters("beautiful");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t11");
						xmlWriter.writeCharacters("place");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t12");
						xmlWriter.writeCharacters(".");
					xmlWriter.writeEndElement();
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_REFERENCES, TCFDictionary.NS_VALUE_TC);
				xmlWriter.writeAttribute(TCFDictionary.ATT_TYPETAGSET, "unknown");
				xmlWriter.writeAttribute(TCFDictionary.ATT_RELTAGSET, "unknown");					
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ENTITY, TCFDictionary.NS_VALUE_TC);//entity "New York"
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_REFERENCE, TCFDictionary.NS_VALUE_TC);//NY-1
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "rc1");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t3 t4");
						xmlWriter.writeAttribute(TCFDictionary.ATT_MINTOKIDS, "t3 t4");//no clearly identified head
						xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "name");
						xmlWriter.writeEndElement();//End of NY-1
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_REFERENCE, TCFDictionary.NS_VALUE_TC);//NY-2
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "rc2");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t6");
						xmlWriter.writeAttribute(TCFDictionary.ATT_MINTOKIDS, "t6");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "pronoun");
						xmlWriter.writeAttribute(TCFDictionary.ATT_REL, "anaphoric");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TARGET, "rc1");
						xmlWriter.writeEndElement();//End of NY-2
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_REFERENCE, TCFDictionary.NS_VALUE_TC);//NY-2
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "rc3");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t8 t9 t10 t11");
						xmlWriter.writeAttribute(TCFDictionary.ATT_MINTOKIDS, "t11");//we choose the nominal head, not the determiner
						xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "noun");
						xmlWriter.writeAttribute(TCFDictionary.ATT_REL, "non-anaphoric");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TARGET, "rc1");
						xmlWriter.writeEndElement();//End of NY-3
					xmlWriter.writeEndElement();//End of entity "New York"
				xmlWriter.writeEndElement();
			xmlWriter.writeEndElement();
		xmlWriter.writeEndElement();
		xmlWriter.writeEndDocument();
	
		/* generating salt sample */
		SDocument doc = SaltFactory.eINSTANCE.createSDocument();
		SDocumentGraph docGraph = SaltFactory.eINSTANCE.createSDocumentGraph();
		doc.setSDocumentGraph(docGraph);
		docGraph.createSTextualDS(EXAMPLE_TEXT_REFERENCE);
		docGraph.tokenize();
		EList<SToken> docTokens = docGraph.getSTokens();
		
		//head marker:
		SAnnotation refHead = SaltFactory.eINSTANCE.createSAnnotation();
		refHead.setName(TCFMapperImport.HEAD_MARKER);
		refHead.setValue(TCFMapperImport.HEAD_MARKER);
		
		//reference layer:
		SLayer docRefLayer = SaltFactory.eINSTANCE.createSLayer();
		docRefLayer.setSName(TCFMapperImport.LAYER_REFERENCES);
		docRefLayer.createSMetaAnnotation(null, TCFDictionary.ATT_TYPETAGSET, "unknown");
		docRefLayer.createSMetaAnnotation(null, TCFDictionary.ATT_RELTAGSET, "unknown");
		
		//relation:
		SPointingRelation reference = SaltFactory.eINSTANCE.createSPointingRelation();
		
		//entity "New York":
		///"New York"
		SSpan newYork = docGraph.createSSpan(docTokens.get(2));
		docGraph.addSNode(newYork, docTokens.get(3), STYPE_NAME.SSPANNING_RELATION);
		newYork.createSAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_TYPE, "name");
		docTokens.get(2).addSAnnotation(refHead);
		docTokens.get(3).addSAnnotation(refHead);
		docRefLayer.getSNodes().add(newYork);		
		///"the most beautiful place"
		SSpan theMostBeautifulPlace = docGraph.createSSpan(docTokens.get(7));
		docGraph.addSNode(theMostBeautifulPlace, docTokens.get(8), STYPE_NAME.SSPANNING_RELATION);
		docGraph.addSNode(theMostBeautifulPlace, docTokens.get(9), STYPE_NAME.SSPANNING_RELATION);
		docGraph.addSNode(theMostBeautifulPlace, docTokens.get(10), STYPE_NAME.SSPANNING_RELATION);		
		theMostBeautifulPlace.createSAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_TYPE, "noun");		
		docTokens.get(10).addSAnnotation(refHead);		
		reference.setSSource(theMostBeautifulPlace);
		reference.setSTarget(newYork);
		reference.createSAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_REL, "non-anaphoric");
		docRefLayer.getSNodes().add(theMostBeautifulPlace);
		docRefLayer.getSRelations().add(reference);
		///"it"
		SSpan it = docGraph.createSSpan(docTokens.get(5));
		it.createSAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_TYPE, "pronoun");		
		it.addSAnnotation(refHead);
		reference = SaltFactory.eINSTANCE.createSPointingRelation();
		reference.setSSource(it);
		reference.setSTarget(newYork);
		reference.createSAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_REL, "anaphoric");
		docRefLayer.getSNodes().add(it);
		docRefLayer.getSRelations().add(reference);
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_REFERENCES);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		this.getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, false);
		
		/* start mapper */
				
		this.getFixture().mapSDocument();
		
		/* compare template salt model to imported salt model */		
		SDocumentGraph fixGraph = this.getFixture().getSDocument().getSDocumentGraph();
		assertNotNull(fixGraph.getSLayerByName(TCFMapperImport.LAYER_REFERENCES));
		SLayer fixRefLayer = fixGraph.getSLayerByName(TCFMapperImport.LAYER_REFERENCES).get(0);
		assertFalse(fixRefLayer.getSNodes().isEmpty());
		assertFalse(fixRefLayer.getSRelations().isEmpty());		
		assertEquals(docRefLayer.getSNodes().size(), fixRefLayer.getSNodes().size());
		EList<SRelation> docReferences = docRefLayer.getSRelations();
		EList<SRelation> fixReferences = fixRefLayer.getSRelations();
		assertEquals(docReferences.size(), fixReferences.size());
		assertNotNull(fixRefLayer.getSMetaAnnotation(TCFDictionary.ATT_TYPETAGSET));
		assertNotNull(fixRefLayer.getSMetaAnnotation(TCFDictionary.ATT_RELTAGSET));
		assertEquals(docRefLayer.getSMetaAnnotation(TCFDictionary.ATT_TYPETAGSET).getValue(), fixRefLayer.getSMetaAnnotation(TCFDictionary.ATT_TYPETAGSET).getValue());
		assertEquals(docRefLayer.getSMetaAnnotation(TCFDictionary.ATT_RELTAGSET).getValue(), fixRefLayer.getSMetaAnnotation(TCFDictionary.ATT_RELTAGSET).getValue());
		
		SRelation docRef = null;
		SRelation fixRef = null;
		for(int i=0; i<docReferences.size(); i++){
			docRef = docReferences.get(i);
			fixRef = fixReferences.get(i);
			/* compare source */
			assertNotNull(fixRef.getSSource());
			assertEquals(docGraph.getSText(docRef.getSSource()), fixGraph.getSText(fixRef.getSSource()));
			//type SSpan?			
			assertTrue(fixRef.getSSource() instanceof SSpan);			
			//compare annotations
			assertNotNull(fixRef.getSSource().getSAnnotation(TCFMapperImport.LAYER_REFERENCES+"::"+TCFDictionary.ATT_TYPE));
			assertEquals(docRef.getSSource().getSAnnotation(TCFMapperImport.LAYER_REFERENCES+"::"+TCFDictionary.ATT_TYPE).getValue(), fixRef.getSSource().getSAnnotation(TCFMapperImport.LAYER_REFERENCES+"::"+TCFDictionary.ATT_TYPE).getValue());			
			/* compare relation */
			assertNotNull(fixRef.getSAnnotation(TCFMapperImport.LAYER_REFERENCES+"::"+TCFDictionary.ATT_REL));
			/* compare target */
			assertNotNull(fixRef.getSTarget());
			assertEquals(docGraph.getSText(docRef.getSTarget()), fixGraph.getSText(fixRef.getSTarget()));
			//type SSpan?
			assertTrue(fixRef.getSSource() instanceof SSpan);
			//compare annotations
			assertNotNull(fixRef.getSTarget().getSAnnotation(TCFMapperImport.LAYER_REFERENCES+"::"+TCFDictionary.ATT_TYPE));
			assertEquals(docRef.getSTarget().getSAnnotation(TCFMapperImport.LAYER_REFERENCES+"::"+TCFDictionary.ATT_TYPE).getValue(), fixRef.getSTarget().getSAnnotation(TCFMapperImport.LAYER_REFERENCES+"::"+TCFDictionary.ATT_TYPE).getValue());			
		}
		/*TODO compare head markings*/
	}
	
//	/**This method tests if a valid TCF-XML-structure containing reference
//	 * annotations is converted to salt correctly by {@link TCFMapperImport} 
//	 * @throws XMLStreamException 
//	 * @throws FileNotFoundException 
//	 */
//	@Test
//	public void testReferencesShrinkedIgnoreIds() throws XMLStreamException, FileNotFoundException{
//		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//		XMLOutputFactory o= XMLOutputFactory.newFactory();
//		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);
//		
//		xmlWriter.writeStartDocument();
//		xmlWriter.writeProcessingInstruction(TCFDictionary.TCF_PI);
//		xmlWriter.writeStartElement(TCFDictionary.NS_WL, TCFDictionary.TAG_WL_D_SPIN, TCFDictionary.NS_VALUE_WL);
//		xmlWriter.writeNamespace(TCFDictionary.NS_WL, TCFDictionary.NS_VALUE_WL);
//		xmlWriter.writeNamespace(TCFDictionary.NS_ED, TCFDictionary.NS_VALUE_ED);
//		xmlWriter.writeNamespace(TCFDictionary.NS_LX, TCFDictionary.NS_VALUE_LX);
//		xmlWriter.writeNamespace(TCFDictionary.NS_MD, TCFDictionary.NS_VALUE_MD);
//		xmlWriter.writeNamespace(TCFDictionary.NS_TC, TCFDictionary.NS_VALUE_TC);
//		xmlWriter.writeAttribute(TCFDictionary.ATT_VERSION, "4.0");
//			xmlWriter.writeStartElement(TCFDictionary.NS_MD, TCFDictionary.TAG_MD_METADATA, TCFDictionary.NS_VALUE_MD);
//			xmlWriter.writeEndElement();
//			xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTCORPUS, TCFDictionary.NS_VALUE_TC);
//				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXT, TCFDictionary.NS_VALUE_TC);
//					xmlWriter.writeCharacters(EXAMPLE_TEXT_REFERENCE);
//				xmlWriter.writeEndElement();
//				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKENS, TCFDictionary.NS_VALUE_TC);
//					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t1");
//						xmlWriter.writeCharacters("I");
//					xmlWriter.writeEndElement();
//					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t2");
//						xmlWriter.writeCharacters("love");
//					xmlWriter.writeEndElement();
//					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t3");
//						xmlWriter.writeCharacters("New");
//					xmlWriter.writeEndElement();
//					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t4");
//						xmlWriter.writeCharacters("York");
//					xmlWriter.writeEndElement();
//					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t5");
//						xmlWriter.writeCharacters(".");
//					xmlWriter.writeEndElement();
//					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t6");
//						xmlWriter.writeCharacters("It");
//					xmlWriter.writeEndElement();
//					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t7");
//						xmlWriter.writeCharacters("is");
//					xmlWriter.writeEndElement();
//					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t8");
//						xmlWriter.writeCharacters("the");
//					xmlWriter.writeEndElement();
//					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t9");
//						xmlWriter.writeCharacters("most");
//					xmlWriter.writeEndElement();
//					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t10");
//						xmlWriter.writeCharacters("beautiful");
//					xmlWriter.writeEndElement();
//					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t11");
//						xmlWriter.writeCharacters("place");
//					xmlWriter.writeEndElement();
//					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t12");
//						xmlWriter.writeCharacters(".");
//					xmlWriter.writeEndElement();
//				xmlWriter.writeEndElement();
//				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_REFERENCES, TCFDictionary.NS_VALUE_TC);
//				xmlWriter.writeAttribute(TCFDictionary.ATT_TYPETAGSET, "unknown");
//				xmlWriter.writeAttribute(TCFDictionary.ATT_RELTAGSET, "unknown");					
//					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ENTITY, TCFDictionary.NS_VALUE_TC);//entity "New York"
//						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_REFERENCE, TCFDictionary.NS_VALUE_TC);//NY-1
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "rc1");
//						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t3 t4");
//						xmlWriter.writeAttribute(TCFDictionary.ATT_MINTOKIDS, "t3 t4");//no clearly identified head
//						xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "name");
//						xmlWriter.writeEndElement();//End of NY-1
//						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_REFERENCE, TCFDictionary.NS_VALUE_TC);//NY-2 ("it")
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "rc1");
//						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t6");
//						xmlWriter.writeAttribute(TCFDictionary.ATT_MINTOKIDS, "t6");
//						xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "pronoun");
//						xmlWriter.writeAttribute(TCFDictionary.ATT_REL, "anaphoric");
//						xmlWriter.writeAttribute(TCFDictionary.ATT_TARGET, "rc1");
//						xmlWriter.writeEndElement();//End of NY-2
//						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_REFERENCE, TCFDictionary.NS_VALUE_TC);//NY-2 ("the most beautiful place")
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "rc1");
//						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t8 t9 t10 t11");
//						xmlWriter.writeAttribute(TCFDictionary.ATT_MINTOKIDS, "t11");//we choose the nominal head, not the determiner
//						xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "noun");
//						xmlWriter.writeAttribute(TCFDictionary.ATT_REL, "non-anaphoric");
//						xmlWriter.writeAttribute(TCFDictionary.ATT_TARGET, "rc1");
//						xmlWriter.writeEndElement();//End of NY-3
//					xmlWriter.writeEndElement();//End of entity "New York"
//				xmlWriter.writeEndElement();
//			xmlWriter.writeEndElement();
//		xmlWriter.writeEndElement();
//		xmlWriter.writeEndDocument();
//	
//		/* generating salt sample */
//		SDocument doc = SaltFactory.eINSTANCE.createSDocument();
//		SDocumentGraph docGraph = SaltFactory.eINSTANCE.createSDocumentGraph();
//		doc.setSDocumentGraph(docGraph);
//		docGraph.createSTextualDS(EXAMPLE_TEXT_REFERENCE);
//		docGraph.tokenize();
//		EList<SToken> docTokens = docGraph.getSTokens();
//		
//		//head marker:
//		SAnnotation refHead = SaltFactory.eINSTANCE.createSAnnotation();
//		refHead.setName(TCFMapperImport.HEAD_MARKER);
//		refHead.setValue(TCFMapperImport.HEAD_MARKER);
//		
//		//reference layer:
//		SLayer docRefLayer = SaltFactory.eINSTANCE.createSLayer();
//		docRefLayer.setSName(TCFMapperImport.LAYER_REFERENCES);
//		docRefLayer.createSMetaAnnotation(null, TCFDictionary.ATT_TYPETAGSET, "unknown");
//		docRefLayer.createSMetaAnnotation(null, TCFDictionary.ATT_RELTAGSET, "unknown");
//		
//		//relation:
//		SPointingRelation reference = SaltFactory.eINSTANCE.createSPointingRelation();
//		
//		//entity "New York":
//		///"New York"
//		SSpan newYork = docGraph.createSSpan(docTokens.get(2));
//		docGraph.addSNode(newYork, docTokens.get(3), STYPE_NAME.SSPANNING_RELATION);
//		newYork.createSAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_TYPE, "name");
//		docTokens.get(2).addSAnnotation(refHead);
//		docTokens.get(3).addSAnnotation(refHead);
//		docRefLayer.getSNodes().add(newYork);		
//		///"the most beautiful place"
//		SSpan theMostBeautifulPlace = docGraph.createSSpan(docTokens.get(7));
//		docGraph.addSNode(theMostBeautifulPlace, docTokens.get(8), STYPE_NAME.SSPANNING_RELATION);
//		docGraph.addSNode(theMostBeautifulPlace, docTokens.get(9), STYPE_NAME.SSPANNING_RELATION);
//		docGraph.addSNode(theMostBeautifulPlace, docTokens.get(10), STYPE_NAME.SSPANNING_RELATION);		
//		theMostBeautifulPlace.createSAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_TYPE, "noun");		
//		docTokens.get(10).addSAnnotation(refHead);
//		reference = (SPointingRelation)docGraph.addSNode(theMostBeautifulPlace, newYork, STYPE_NAME.SPOINTING_RELATION);
//		reference.createSAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_REL, "non-anaphoric");
//		docRefLayer.getSNodes().add(theMostBeautifulPlace);
//		docRefLayer.getSRelations().add(reference);
//		///"it"
//		docTokens.get(5).createSAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_TYPE, "pronoun");		
//		docTokens.get(5).addSAnnotation(refHead);
//		reference = (SPointingRelation)docGraph.addSNode(docTokens.get(5), newYork, STYPE_NAME.SPOINTING_RELATION);
//		reference.createSAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_REL, "anaphoric");
//		docRefLayer.getSNodes().add(docTokens.get(5));
//		docRefLayer.getSRelations().add(reference);
//		
//		/* setting variables */		
//		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_REFERENCES);
//		tmpOut.getParentFile().mkdirs();
//		PrintWriter p = new PrintWriter(tmpOut);		
//		p.println(outStream.toString());
//		p.close();
//		this.getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
//		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, true);
//		
//		/* start mapper */
//				
//		this.getFixture().mapSDocument();
//		
//		//		//		//		//		//		//		//		//		
//		/* compare template salt model to imported salt model */		
//		SDocumentGraph fixGraph = this.getFixture().getSDocument().getSDocumentGraph();
//		assertNotNull(fixGraph.getSLayerByName(TCFMapperImport.LAYER_REFERENCES));
//		SLayer fixRefLayer = fixGraph.getSLayerByName(TCFMapperImport.LAYER_REFERENCES).get(0);
//		assertFalse(fixRefLayer.getSNodes().isEmpty());
//		assertFalse(fixRefLayer.getSRelations().isEmpty());		
//		assertEquals(docRefLayer.getSNodes().size(), fixRefLayer.getSNodes().size());
//		EList<SRelation> docReferences = docRefLayer.getSRelations();
//		EList<SRelation> fixReferences = fixRefLayer.getSRelations();
//		assertEquals(docReferences.size(), fixReferences.size());
//		assertNotNull(fixRefLayer.getSMetaAnnotation(TCFDictionary.ATT_TYPETAGSET));
//		assertNotNull(fixRefLayer.getSMetaAnnotation(TCFDictionary.ATT_RELTAGSET));
//		assertEquals(docRefLayer.getSMetaAnnotation(TCFDictionary.ATT_TYPETAGSET).getValue(), fixRefLayer.getSMetaAnnotation(TCFDictionary.ATT_TYPETAGSET).getValue());
//		assertEquals(docRefLayer.getSMetaAnnotation(TCFDictionary.ATT_RELTAGSET).getValue(), fixRefLayer.getSMetaAnnotation(TCFDictionary.ATT_RELTAGSET).getValue());
//		
//		StringBuilder tree = new StringBuilder();
//		int j;
//		//		for(SNode sNode : fixGraph.getSNodes()){
//			tree.append(fixGraph.getSText(sNode));
//			tree.append("--");
//			j=0;
//			if(sNode.getOutgoingSRelations().size()>0){
//				while(!(sNode.getOutgoingSRelations().get(j) instanceof SPointingRelation)){
//					j++;
//					if(j==sNode.getOutgoingSRelations().size()){break;}				
//				}
//				if(j<sNode.getOutgoingSRelations().size()){
//					tree.append(sNode.getOutgoingSRelations().get(j).getSAnnotation(TCFMapperImport.LAYER_REFERENCES+"::"+TCFDictionary.ATT_REL).getValue());
//					tree.append("->");
//					tree.append(fixGraph.getSText((SNode)sNode.getOutgoingSRelations().get(j).getTarget()));
//													
//				}
//			}
//			tree.delete(0, tree.length());
//		}
//		//doc
//		//		for(SNode sNode : docGraph.getSNodes()){
//			tree.append(docGraph.getSText(sNode));
//			tree.append("--");
//			j=0;
//			if(sNode.getOutgoingSRelations().size()>0){
//				while(!(sNode.getOutgoingSRelations().get(j) instanceof SPointingRelation)){
//					j++;
//					if(j==sNode.getOutgoingSRelations().size()){break;}				
//				}
//				if(j<sNode.getOutgoingSRelations().size()){
//					tree.append(sNode.getOutgoingSRelations().get(j).getSAnnotation(TCFMapperImport.LAYER_REFERENCES+"::"+TCFDictionary.ATT_REL).getValue());
//					tree.append("->");
//					tree.append(docGraph.getSText((SNode)sNode.getOutgoingSRelations().get(j).getTarget()));
//													
//				}
//			}
//			tree.delete(0, tree.length());
//		}
//		
//		SRelation docRef = null;
//		SRelation fixRef = null;
//		for(int i=0; i<docReferences.size(); i++){
//			docRef = docReferences.get(i);
//			fixRef = fixReferences.get(i);
//			/* compare source */
//			assertNotNull(fixRef.getSSource());
//			assertEquals(docGraph.getSText(docRef.getSSource()), fixGraph.getSText(fixRef.getSSource()));
//			assertNotNull(fixRef.getSSource().getSAnnotation(TCFMapperImport.LAYER_REFERENCES+"::"+TCFDictionary.ATT_TYPE));
//			assertEquals(docRef.getSSource().getSAnnotation(TCFMapperImport.LAYER_REFERENCES+"::"+TCFDictionary.ATT_TYPE).getValue(), fixRef.getSSource().getSAnnotation(TCFMapperImport.LAYER_REFERENCES+"::"+TCFDictionary.ATT_TYPE).getValue());
//			assertEquals(docRef.getSSource().getClass(), fixRef.getSSource().getClass());
//			/* compare relation */
//			assertNotNull(fixRef.getSAnnotation(TCFMapperImport.LAYER_REFERENCES+"::"+TCFDictionary.ATT_REL));
//			/* compare target */
//			assertNotNull(fixRef.getSTarget());
//			assertEquals(docGraph.getSText(docRef.getSTarget()), fixGraph.getSText(fixRef.getSTarget()));
//			assertNotNull(fixRef.getSTarget().getSAnnotation(TCFMapperImport.LAYER_REFERENCES+"::"+TCFDictionary.ATT_TYPE));
//			assertEquals(docRef.getSTarget().getSAnnotation(TCFMapperImport.LAYER_REFERENCES+"::"+TCFDictionary.ATT_TYPE).getValue(), fixRef.getSTarget().getSAnnotation(TCFMapperImport.LAYER_REFERENCES+"::"+TCFDictionary.ATT_TYPE).getValue());
//			assertEquals(docRef.getSTarget().getClass(), fixRef.getSTarget().getClass());
//		}
//		/*TODO compare head markings*/
//	}
//	
//	/**This method tests if a valid TCF-XML-structure containing reference
//	 * annotations is converted to salt correctly by {@link TCFMapperImport} 
//	 * @throws XMLStreamException 
//	 * @throws FileNotFoundException 
//	 */
//	@Test
//	public void testReferencesNotShrinkedIgnoreIds() throws XMLStreamException, FileNotFoundException{
//		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//		XMLOutputFactory o= XMLOutputFactory.newFactory();
//		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);
//		
//		xmlWriter.writeStartDocument();
//		xmlWriter.writeProcessingInstruction(TCFDictionary.TCF_PI);
//		xmlWriter.writeStartElement(TCFDictionary.NS_WL, TCFDictionary.TAG_WL_D_SPIN, TCFDictionary.NS_VALUE_WL);
//		xmlWriter.writeNamespace(TCFDictionary.NS_WL, TCFDictionary.NS_VALUE_WL);
//		xmlWriter.writeNamespace(TCFDictionary.NS_ED, TCFDictionary.NS_VALUE_ED);
//		xmlWriter.writeNamespace(TCFDictionary.NS_LX, TCFDictionary.NS_VALUE_LX);
//		xmlWriter.writeNamespace(TCFDictionary.NS_MD, TCFDictionary.NS_VALUE_MD);
//		xmlWriter.writeNamespace(TCFDictionary.NS_TC, TCFDictionary.NS_VALUE_TC);
//		xmlWriter.writeAttribute(TCFDictionary.ATT_VERSION, "4.0");
//			xmlWriter.writeStartElement(TCFDictionary.NS_MD, TCFDictionary.TAG_MD_METADATA, TCFDictionary.NS_VALUE_MD);
//			xmlWriter.writeEndElement();
//			xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTCORPUS, TCFDictionary.NS_VALUE_TC);
//				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXT, TCFDictionary.NS_VALUE_TC);
//					xmlWriter.writeCharacters(EXAMPLE_TEXT_REFERENCE);
//				xmlWriter.writeEndElement();
//				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKENS, TCFDictionary.NS_VALUE_TC);
//					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t1");
//						xmlWriter.writeCharacters("I");
//					xmlWriter.writeEndElement();
//					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t2");
//						xmlWriter.writeCharacters("love");
//					xmlWriter.writeEndElement();
//					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t3");
//						xmlWriter.writeCharacters("New");
//					xmlWriter.writeEndElement();
//					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t4");
//						xmlWriter.writeCharacters("York");
//					xmlWriter.writeEndElement();
//					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t5");
//						xmlWriter.writeCharacters(".");
//					xmlWriter.writeEndElement();
//					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t6");
//						xmlWriter.writeCharacters("It");
//					xmlWriter.writeEndElement();
//					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t7");
//						xmlWriter.writeCharacters("is");
//					xmlWriter.writeEndElement();
//					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t8");
//						xmlWriter.writeCharacters("the");
//					xmlWriter.writeEndElement();
//					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t9");
//						xmlWriter.writeCharacters("most");
//					xmlWriter.writeEndElement();
//					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t10");
//						xmlWriter.writeCharacters("beautiful");
//					xmlWriter.writeEndElement();
//					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t11");
//						xmlWriter.writeCharacters("place");
//					xmlWriter.writeEndElement();
//					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t12");
//						xmlWriter.writeCharacters(".");
//					xmlWriter.writeEndElement();
//				xmlWriter.writeEndElement();
//				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_REFERENCES, TCFDictionary.NS_VALUE_TC);
//				xmlWriter.writeAttribute(TCFDictionary.ATT_TYPETAGSET, "unknown");
//				xmlWriter.writeAttribute(TCFDictionary.ATT_RELTAGSET, "unknown");					
//					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ENTITY, TCFDictionary.NS_VALUE_TC);//entity "New York"
//						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_REFERENCE, TCFDictionary.NS_VALUE_TC);//NY-1
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "rc1");
//						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t3 t4");
//						xmlWriter.writeAttribute(TCFDictionary.ATT_MINTOKIDS, "t3 t4");//no clearly identified head
//						xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "name");
//						xmlWriter.writeEndElement();//End of NY-1
//						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_REFERENCE, TCFDictionary.NS_VALUE_TC);//NY-2
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "rc1");
//						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t6");
//						xmlWriter.writeAttribute(TCFDictionary.ATT_MINTOKIDS, "t6");
//						xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "pronoun");
//						xmlWriter.writeAttribute(TCFDictionary.ATT_REL, "anaphoric");
//						xmlWriter.writeAttribute(TCFDictionary.ATT_TARGET, "rc1");
//						xmlWriter.writeEndElement();//End of NY-2
//						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_REFERENCE, TCFDictionary.NS_VALUE_TC);//NY-2
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "rc1");
//						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t8 t9 t10 t11");
//						xmlWriter.writeAttribute(TCFDictionary.ATT_MINTOKIDS, "t11");//we choose the nominal head, not the determiner
//						xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "noun");
//						xmlWriter.writeAttribute(TCFDictionary.ATT_REL, "non-anaphoric");
//						xmlWriter.writeAttribute(TCFDictionary.ATT_TARGET, "rc1");
//						xmlWriter.writeEndElement();//End of NY-3
//					xmlWriter.writeEndElement();//End of entity "New York"
//				xmlWriter.writeEndElement();
//			xmlWriter.writeEndElement();
//		xmlWriter.writeEndElement();
//		xmlWriter.writeEndDocument();
//	
//		/* generating salt sample */
//		SDocument doc = SaltFactory.eINSTANCE.createSDocument();
//		SDocumentGraph docGraph = SaltFactory.eINSTANCE.createSDocumentGraph();
//		doc.setSDocumentGraph(docGraph);
//		docGraph.createSTextualDS(EXAMPLE_TEXT_REFERENCE);
//		docGraph.tokenize();
//		EList<SToken> docTokens = docGraph.getSTokens();
//		
//		//head marker:
//		SAnnotation refHead = SaltFactory.eINSTANCE.createSAnnotation();
//		refHead.setName(TCFMapperImport.HEAD_MARKER);
//		refHead.setValue(TCFMapperImport.HEAD_MARKER);
//		
//		//reference layer:
//		SLayer docRefLayer = SaltFactory.eINSTANCE.createSLayer();
//		docRefLayer.setSName(TCFMapperImport.LAYER_REFERENCES);
//		docRefLayer.createSMetaAnnotation(null, TCFDictionary.ATT_TYPETAGSET, "unknown");
//		docRefLayer.createSMetaAnnotation(null, TCFDictionary.ATT_RELTAGSET, "unknown");
//		
//		//relation:
//		SPointingRelation reference = SaltFactory.eINSTANCE.createSPointingRelation();
//		
//		//entity "New York":
//		///"New York"
//		SSpan newYork = docGraph.createSSpan(docTokens.get(2));
//		docGraph.addSNode(newYork, docTokens.get(3), STYPE_NAME.SSPANNING_RELATION);
//		newYork.createSAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_TYPE, "name");
//		docTokens.get(2).addSAnnotation(refHead);
//		docTokens.get(3).addSAnnotation(refHead);
//		docRefLayer.getSNodes().add(newYork);		
//		///"the most beautiful place"
//		SSpan theMostBeautifulPlace = docGraph.createSSpan(docTokens.get(7));
//		docGraph.addSNode(theMostBeautifulPlace, docTokens.get(8), STYPE_NAME.SSPANNING_RELATION);
//		docGraph.addSNode(theMostBeautifulPlace, docTokens.get(9), STYPE_NAME.SSPANNING_RELATION);
//		docGraph.addSNode(theMostBeautifulPlace, docTokens.get(10), STYPE_NAME.SSPANNING_RELATION);		
//		theMostBeautifulPlace.createSAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_TYPE, "noun");		
//		docTokens.get(10).addSAnnotation(refHead);		
//		reference.setSSource(theMostBeautifulPlace);
//		reference.setSTarget(newYork);
//		reference.createSAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_REL, "non-anaphoric");
//		docRefLayer.getSNodes().add(theMostBeautifulPlace);
//		docRefLayer.getSRelations().add(reference);
//		///"it"
//		SSpan it = docGraph.createSSpan(docTokens.get(5));
//		it.createSAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_TYPE, "pronoun");		
//		it.addSAnnotation(refHead);
//		reference = SaltFactory.eINSTANCE.createSPointingRelation();
//		reference.setSSource(it);
//		reference.setSTarget(newYork);
//		reference.createSAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_REL, "anaphoric");
//		docRefLayer.getSNodes().add(it);
//		docRefLayer.getSRelations().add(reference);
//		
//		/* setting variables */		
//		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_REFERENCES);
//		tmpOut.getParentFile().mkdirs();
//		PrintWriter p = new PrintWriter(tmpOut);		
//		p.println(outStream.toString());
//		p.close();
//		this.getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
//		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, false);
//		
//		/* start mapper */
//				
//		this.getFixture().mapSDocument();
//		
//		/* compare template salt model to imported salt model */		
//		SDocumentGraph fixGraph = this.getFixture().getSDocument().getSDocumentGraph();
//		assertNotNull(fixGraph.getSLayerByName(TCFMapperImport.LAYER_REFERENCES));
//		SLayer fixRefLayer = fixGraph.getSLayerByName(TCFMapperImport.LAYER_REFERENCES).get(0);
//		assertFalse(fixRefLayer.getSNodes().isEmpty());
//		assertFalse(fixRefLayer.getSRelations().isEmpty());		
//		assertEquals(docRefLayer.getSNodes().size(), fixRefLayer.getSNodes().size());
//		EList<SRelation> docReferences = docRefLayer.getSRelations();
//		EList<SRelation> fixReferences = fixRefLayer.getSRelations();
//		assertEquals(docReferences.size(), fixReferences.size());
//		assertNotNull(fixRefLayer.getSMetaAnnotation(TCFDictionary.ATT_TYPETAGSET));
//		assertNotNull(fixRefLayer.getSMetaAnnotation(TCFDictionary.ATT_RELTAGSET));
//		assertEquals(docRefLayer.getSMetaAnnotation(TCFDictionary.ATT_TYPETAGSET).getValue(), fixRefLayer.getSMetaAnnotation(TCFDictionary.ATT_TYPETAGSET).getValue());
//		assertEquals(docRefLayer.getSMetaAnnotation(TCFDictionary.ATT_RELTAGSET).getValue(), fixRefLayer.getSMetaAnnotation(TCFDictionary.ATT_RELTAGSET).getValue());
//		
//		SRelation docRef = null;
//		SRelation fixRef = null;
//		for(int i=0; i<docReferences.size(); i++){
//			docRef = docReferences.get(i);
//			fixRef = fixReferences.get(i);
//			/* compare source */
//			assertNotNull(fixRef.getSSource());
//			assertEquals(docGraph.getSText(docRef.getSSource()), fixGraph.getSText(fixRef.getSSource()));
//			//type SSpan?			
//			assertTrue(fixRef.getSSource() instanceof SSpan);			
//			//compare annotations
//			assertNotNull(fixRef.getSSource().getSAnnotation(TCFMapperImport.LAYER_REFERENCES+"::"+TCFDictionary.ATT_TYPE));
//			assertEquals(docRef.getSSource().getSAnnotation(TCFMapperImport.LAYER_REFERENCES+"::"+TCFDictionary.ATT_TYPE).getValue(), fixRef.getSSource().getSAnnotation(TCFMapperImport.LAYER_REFERENCES+"::"+TCFDictionary.ATT_TYPE).getValue());			
//			/* compare relation */
//			assertNotNull(fixRef.getSAnnotation(TCFMapperImport.LAYER_REFERENCES+"::"+TCFDictionary.ATT_REL));
//			/* compare target */
//			assertNotNull(fixRef.getSTarget());
//			assertEquals(docGraph.getSText(docRef.getSTarget()), fixGraph.getSText(fixRef.getSTarget()));
//			//type SSpan?
//			assertTrue(fixRef.getSSource() instanceof SSpan);
//			//compare annotations
//			assertNotNull(fixRef.getSTarget().getSAnnotation(TCFMapperImport.LAYER_REFERENCES+"::"+TCFDictionary.ATT_TYPE));
//			assertEquals(docRef.getSTarget().getSAnnotation(TCFMapperImport.LAYER_REFERENCES+"::"+TCFDictionary.ATT_TYPE).getValue(), fixRef.getSTarget().getSAnnotation(TCFMapperImport.LAYER_REFERENCES+"::"+TCFDictionary.ATT_TYPE).getValue());			
//		}
//		/*TODO compare head markings*/
//	}
	
	/**This method tests if a valid TCF-XML-structure containing phonetic
	 * annotation is converted to salt correctly
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testPhoneticsShrinked() throws XMLStreamException, FileNotFoundException{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o= XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);
		
		xmlWriter.writeStartDocument();
		xmlWriter.writeProcessingInstruction(TCFDictionary.TCF_PI);
		xmlWriter.writeStartElement(TCFDictionary.NS_WL, TCFDictionary.TAG_WL_D_SPIN, TCFDictionary.NS_VALUE_WL);
		xmlWriter.writeNamespace(TCFDictionary.NS_WL, TCFDictionary.NS_VALUE_WL);
		xmlWriter.writeNamespace(TCFDictionary.NS_ED, TCFDictionary.NS_VALUE_ED);
		xmlWriter.writeNamespace(TCFDictionary.NS_LX, TCFDictionary.NS_VALUE_LX);
		xmlWriter.writeNamespace(TCFDictionary.NS_MD, TCFDictionary.NS_VALUE_MD);
		xmlWriter.writeNamespace(TCFDictionary.NS_TC, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_VERSION, "4.0");
			xmlWriter.writeStartElement(TCFDictionary.NS_MD, TCFDictionary.TAG_MD_METADATA, TCFDictionary.NS_VALUE_MD);
			xmlWriter.writeEndElement();
			xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTCORPUS, TCFDictionary.NS_VALUE_TC);
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXT, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeCharacters(EXAMPLE_TEXT_SHRINK);
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKENS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t1");
						xmlWriter.writeCharacters("I");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t2");
						xmlWriter.writeCharacters("love");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t3");
						xmlWriter.writeCharacters("New");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t4");
						xmlWriter.writeCharacters("York");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t5");
						xmlWriter.writeCharacters(".");
					xmlWriter.writeEndElement();					
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_PHONETICS, TCFDictionary.NS_VALUE_TC);
				xmlWriter.writeAttribute(TCFDictionary.ATT_TRANSCRIPTION, "IPA");
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_PRON, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKID, "t1");
					xmlWriter.writeCharacters("ʔa͜ɪ");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_PRON, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKID, "t2");
					xmlWriter.writeCharacters("lʌv");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_PRON, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKID, "t3");
					xmlWriter.writeCharacters("nu");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_PRON, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKID, "t4");
					xmlWriter.writeCharacters("jɔɹk");
					xmlWriter.writeEndElement();
				xmlWriter.writeEndElement();
			xmlWriter.writeEndElement();
		xmlWriter.writeEndElement();
		xmlWriter.writeEndDocument();
		
		/* generating salt sample */
		SDocument doc = SaltFactory.eINSTANCE.createSDocument();
		SDocumentGraph docGraph = SaltFactory.eINSTANCE.createSDocumentGraph();
		doc.setSDocumentGraph(docGraph);
		docGraph.createSTextualDS(EXAMPLE_TEXT_SHRINK);
		docGraph.tokenize();
		EList<SToken> docTokens = docGraph.getSTokens();
		SLayer docPhoLayer = SaltFactory.eINSTANCE.createSLayer();
		docPhoLayer.setSName(TCFMapperImport.LAYER_PHONETICS);
		docGraph.addSLayer(docPhoLayer);
		EList<SNode> docPhoNodes = docPhoLayer.getSNodes();
		
		docTokens.get(0).createSAnnotation(TCFMapperImport.LAYER_PHONETICS, TCFDictionary.TAG_TC_PRON, "ʔa͜ɪ");
		docTokens.get(1).createSAnnotation(TCFMapperImport.LAYER_PHONETICS, TCFDictionary.TAG_TC_PRON, "lʌv");
		docTokens.get(2).createSAnnotation(TCFMapperImport.LAYER_PHONETICS, TCFDictionary.TAG_TC_PRON, "nu");
		docTokens.get(3).createSAnnotation(TCFMapperImport.LAYER_PHONETICS, TCFDictionary.TAG_TC_PRON, "jɔɹk");
		docPhoNodes.add(docTokens.get(0));
		docPhoNodes.add(docTokens.get(1));
		docPhoNodes.add(docTokens.get(2));
		docPhoNodes.add(docTokens.get(3));
		docPhoLayer.createSMetaAnnotation(null, TCFDictionary.ATT_TRANSCRIPTION, "IPA");
				
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_PHONETICS);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		this.getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, true);
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, false);
		
		/* start mapper */
				
		this.getFixture().mapSDocument();
		
		
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = this.getFixture().getSDocument().getSDocumentGraph();		
		assertFalse(fixGraph.getSLayerByName(TCFMapperImport.LAYER_PHONETICS).isEmpty());
		SLayer fixPhoLayer = fixGraph.getSLayerByName(TCFMapperImport.LAYER_PHONETICS).get(0);
		assertNotNull(fixPhoLayer.getSMetaAnnotation(TCFDictionary.ATT_TRANSCRIPTION));
		assertEquals(docPhoLayer.getSMetaAnnotation(TCFDictionary.ATT_TRANSCRIPTION).getValue(), fixPhoLayer.getSMetaAnnotation(TCFDictionary.ATT_TRANSCRIPTION).getValue());
		EList<SNode> fixPhoNodes = fixPhoLayer.getSNodes();		
		assertFalse(fixPhoNodes.isEmpty());
		assertEquals(docPhoNodes.size(), fixPhoNodes.size());
		SToken docTok = null;
		SToken fixTok = null;
		for(int i=0; i<docPhoNodes.size(); i++){
			docTok = (SToken)docPhoNodes.get(i);
			fixTok = (SToken)fixPhoNodes.get(i);
			assertEquals(docGraph.getSText(docTok), fixGraph.getSText(fixTok));
			assertNotNull(fixTok.getSAnnotation(TCFMapperImport.LAYER_PHONETICS+"::"+TCFDictionary.TAG_TC_PRON));
			assertEquals(docTok.getSAnnotation(TCFMapperImport.LAYER_PHONETICS+"::"+TCFDictionary.TAG_TC_PRON).getValue(), fixTok.getSAnnotation(TCFMapperImport.LAYER_PHONETICS+"::"+TCFDictionary.TAG_TC_PRON).getValue());
		}
	}

	/**This method tests if a valid TCF-XML-structure containing phonetic
	 * annotation is converted to salt correctly
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testPhoneticsNotShrinked() throws XMLStreamException, FileNotFoundException{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o= XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);
		
		xmlWriter.writeStartDocument();
		xmlWriter.writeProcessingInstruction(TCFDictionary.TCF_PI);
		xmlWriter.writeStartElement(TCFDictionary.NS_WL, TCFDictionary.TAG_WL_D_SPIN, TCFDictionary.NS_VALUE_WL);
		xmlWriter.writeNamespace(TCFDictionary.NS_WL, TCFDictionary.NS_VALUE_WL);
		xmlWriter.writeNamespace(TCFDictionary.NS_ED, TCFDictionary.NS_VALUE_ED);
		xmlWriter.writeNamespace(TCFDictionary.NS_LX, TCFDictionary.NS_VALUE_LX);
		xmlWriter.writeNamespace(TCFDictionary.NS_MD, TCFDictionary.NS_VALUE_MD);
		xmlWriter.writeNamespace(TCFDictionary.NS_TC, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_VERSION, "4.0");
			xmlWriter.writeStartElement(TCFDictionary.NS_MD, TCFDictionary.TAG_MD_METADATA, TCFDictionary.NS_VALUE_MD);
			xmlWriter.writeEndElement();
			xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTCORPUS, TCFDictionary.NS_VALUE_TC);
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXT, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeCharacters(EXAMPLE_TEXT_SHRINK);
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKENS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t1");
						xmlWriter.writeCharacters("I");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t2");
						xmlWriter.writeCharacters("love");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t3");
						xmlWriter.writeCharacters("New");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t4");
						xmlWriter.writeCharacters("York");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t5");
						xmlWriter.writeCharacters(".");
					xmlWriter.writeEndElement();					
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_PHONETICS, TCFDictionary.NS_VALUE_TC);
				xmlWriter.writeAttribute(TCFDictionary.ATT_TRANSCRIPTION, "IPA");
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_PRON, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKID, "t1");
					xmlWriter.writeCharacters("ʔa͜ɪ");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_PRON, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKID, "t2");
					xmlWriter.writeCharacters("lʌv");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_PRON, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKID, "t3");
					xmlWriter.writeCharacters("nu");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_PRON, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKID, "t4");
					xmlWriter.writeCharacters("jɔɹk");
					xmlWriter.writeEndElement();
				xmlWriter.writeEndElement();
			xmlWriter.writeEndElement();
		xmlWriter.writeEndElement();
		xmlWriter.writeEndDocument();
		
		/* generating salt sample */
		SDocument doc = SaltFactory.eINSTANCE.createSDocument();
		SDocumentGraph docGraph = SaltFactory.eINSTANCE.createSDocumentGraph();
		doc.setSDocumentGraph(docGraph);
		docGraph.createSTextualDS(EXAMPLE_TEXT_SHRINK);
		docGraph.tokenize();
		EList<SToken> docTokens = docGraph.getSTokens();
		SLayer docPhoLayer = SaltFactory.eINSTANCE.createSLayer();
		docPhoLayer.setSName(TCFMapperImport.LAYER_PHONETICS);
		docGraph.addSLayer(docPhoLayer);
		EList<SNode> docPhoNodes = docPhoLayer.getSNodes();
		
		SSpan sSpan = docGraph.createSSpan(docTokens.get(0));
		docPhoNodes.add(sSpan);
		sSpan.createSAnnotation(TCFMapperImport.LAYER_PHONETICS, TCFDictionary.TAG_TC_PRON, "ʔa͜ɪ");
		sSpan = docGraph.createSSpan(docTokens.get(1));
		docPhoNodes.add(sSpan);
		sSpan.createSAnnotation(TCFMapperImport.LAYER_PHONETICS, TCFDictionary.TAG_TC_PRON, "lʌv");
		sSpan = docGraph.createSSpan(docTokens.get(2));
		docPhoNodes.add(sSpan);
		sSpan.createSAnnotation(TCFMapperImport.LAYER_PHONETICS, TCFDictionary.TAG_TC_PRON, "nu");
		sSpan = docGraph.createSSpan(docTokens.get(3));
		docPhoNodes.add(sSpan);
		sSpan.createSAnnotation(TCFMapperImport.LAYER_PHONETICS, TCFDictionary.TAG_TC_PRON, "jɔɹk");
		docPhoLayer.createSMetaAnnotation(null, TCFDictionary.ATT_TRANSCRIPTION, "IPA");
				
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_PHONETICS);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		this.getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, false);
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, false);
		
		/* start mapper */
				
		this.getFixture().mapSDocument();
		
		
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = this.getFixture().getSDocument().getSDocumentGraph();		
		assertFalse(fixGraph.getSLayerByName(TCFMapperImport.LAYER_PHONETICS).isEmpty());
		SLayer fixPhoLayer = fixGraph.getSLayerByName(TCFMapperImport.LAYER_PHONETICS).get(0);
		assertNotNull(fixPhoLayer.getSMetaAnnotation(TCFDictionary.ATT_TRANSCRIPTION));
		assertEquals(docPhoLayer.getSMetaAnnotation(TCFDictionary.ATT_TRANSCRIPTION).getValue(), fixPhoLayer.getSMetaAnnotation(TCFDictionary.ATT_TRANSCRIPTION).getValue());
		EList<SNode> fixPhoNodes = fixPhoLayer.getSNodes();		
		assertFalse(fixPhoNodes.isEmpty());
		assertEquals(docPhoNodes.size(), fixPhoNodes.size());
		SNode docNode = null;
		SNode fixNode = null;
		for(int i=0; i<docPhoNodes.size(); i++){
			docNode = docPhoNodes.get(i);
			fixNode = fixPhoNodes.get(i);
			assertTrue(fixNode instanceof SSpan);
			assertEquals(docGraph.getSText(docNode), fixGraph.getSText(fixNode));
			assertNotNull(fixNode.getSAnnotation(TCFMapperImport.LAYER_PHONETICS+"::"+TCFDictionary.TAG_TC_PRON));
			if(DEBUG){
				}
			assertEquals(docNode.getSAnnotation(TCFMapperImport.LAYER_PHONETICS+"::"+TCFDictionary.TAG_TC_PRON).getValue(), fixNode.getSAnnotation(TCFMapperImport.LAYER_PHONETICS+"::"+TCFDictionary.TAG_TC_PRON).getValue());
		}
	}

	/**This method tests if a valid TCF-XML-structure containing orthography
	 * annotations is converted to salt correctly
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testOrthographyShrinked() throws XMLStreamException, FileNotFoundException{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o= XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);
		
		xmlWriter.writeStartDocument();
		xmlWriter.writeProcessingInstruction(TCFDictionary.TCF_PI);
		xmlWriter.writeStartElement(TCFDictionary.NS_WL, TCFDictionary.TAG_WL_D_SPIN, TCFDictionary.NS_VALUE_WL);
		xmlWriter.writeNamespace(TCFDictionary.NS_WL, TCFDictionary.NS_VALUE_WL);
		xmlWriter.writeNamespace(TCFDictionary.NS_ED, TCFDictionary.NS_VALUE_ED);
		xmlWriter.writeNamespace(TCFDictionary.NS_LX, TCFDictionary.NS_VALUE_LX);
		xmlWriter.writeNamespace(TCFDictionary.NS_MD, TCFDictionary.NS_VALUE_MD);
		xmlWriter.writeNamespace(TCFDictionary.NS_TC, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_VERSION, "4.0");
			xmlWriter.writeStartElement(TCFDictionary.NS_MD, TCFDictionary.TAG_MD_METADATA, TCFDictionary.NS_VALUE_MD);
			xmlWriter.writeEndElement();
			xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTCORPUS, TCFDictionary.NS_VALUE_TC);
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXT, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeCharacters(EXAMPLE_TEXT_ORTHOGRAPHY);
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKENS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t1");
						xmlWriter.writeCharacters("Ei");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t2");
						xmlWriter.writeCharacters("laaf");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t3");
						xmlWriter.writeCharacters("Nuh");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t4");
						xmlWriter.writeCharacters("Jork");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t5");
						xmlWriter.writeCharacters(".");
					xmlWriter.writeEndElement();					
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ORTHOGRAPHY, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CORRECTION, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_OPERATION, "replace");
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t1");
					xmlWriter.writeCharacters("I");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CORRECTION, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_OPERATION, "replace");
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t2");
					xmlWriter.writeCharacters("love");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CORRECTION, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_OPERATION, "replace");
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t3 t4");
					xmlWriter.writeCharacters("New York");
					xmlWriter.writeEndElement();
				xmlWriter.writeEndElement();
			xmlWriter.writeEndElement();
		xmlWriter.writeEndElement();
		xmlWriter.writeEndDocument();
		
		/* generating salt sample */
		SDocument doc = SaltFactory.eINSTANCE.createSDocument();
		SDocumentGraph docGraph = SaltFactory.eINSTANCE.createSDocumentGraph();
		doc.setSDocumentGraph(docGraph);
		docGraph.createSTextualDS(EXAMPLE_TEXT_ORTHOGRAPHY);
		docGraph.tokenize();
		EList<SToken> docTokens = docGraph.getSTokens();
		SLayer orthLayer = SaltFactory.eINSTANCE.createSLayer();
		orthLayer.setSName(TCFMapperImport.LAYER_ORTHOGRAPHY);
		docGraph.addSLayer(orthLayer);
		EList<SNode> docOrthNodes = orthLayer.getSNodes();
		
		SAnnotation anno = null;
		SAnnotation operation = null;
		
		anno = docTokens.get(0).createSAnnotation(TCFMapperImport.LAYER_ORTHOGRAPHY, TCFDictionary.TAG_TC_CORRECTION, "I");
		operation = SaltFactory.eINSTANCE.createSAnnotation();
		operation.setNamespace(TCFMapperImport.LAYER_ORTHOGRAPHY);
		operation.setName(TCFDictionary.ATT_OPERATION);
		operation.setValue("replace");
		anno.addLabel(operation);
		docOrthNodes.add(docTokens.get(0));
		
		anno = docTokens.get(1).createSAnnotation(TCFMapperImport.LAYER_ORTHOGRAPHY, TCFDictionary.TAG_TC_CORRECTION, "love");
		operation = SaltFactory.eINSTANCE.createSAnnotation();
		operation.setNamespace(TCFMapperImport.LAYER_ORTHOGRAPHY);
		operation.setName(TCFDictionary.ATT_OPERATION);
		operation.setValue("replace");
		anno.addLabel(operation);
		docOrthNodes.add(docTokens.get(1));
		
		SSpan newYork = docGraph.createSSpan(docTokens.get(2));
		docGraph.addSNode(newYork, docTokens.get(3), STYPE_NAME.SSPANNING_RELATION);
		anno = newYork.createSAnnotation(TCFMapperImport.LAYER_ORTHOGRAPHY, TCFDictionary.TAG_TC_CORRECTION, "New York");		
		operation = SaltFactory.eINSTANCE.createSAnnotation();
		operation.setNamespace(TCFMapperImport.LAYER_ORTHOGRAPHY);
		operation.setName(TCFDictionary.ATT_OPERATION);
		operation.setValue("replace");
		anno.addLabel(operation);
		docOrthNodes.add(newYork);
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_ORTHOGRAPHY);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		this.getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, true);
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, false);
		
		/* start mapper */
				
		this.getFixture().mapSDocument();
		
		
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = this.getFixture().getSDocument().getSDocumentGraph();		
		assertFalse(fixGraph.getSLayerByName(TCFMapperImport.LAYER_ORTHOGRAPHY).isEmpty());
		EList<SNode> fixOrthNodes = fixGraph.getSLayerByName(TCFMapperImport.LAYER_ORTHOGRAPHY).get(0).getSNodes();
		assertEquals(docOrthNodes.size(), fixOrthNodes.size());
		SNode docNode = null;
		SNode fixNode = null;
		for(int i=0; i<docOrthNodes.size(); i++){
			docNode = docOrthNodes.get(i);
			fixNode = fixOrthNodes.get(i);			
			assertEquals(docNode.getClass(), fixNode.getClass());
			assertEquals(docGraph.getSText(docNode), fixGraph.getSText(fixNode));
			anno = fixNode.getSAnnotation(TCFMapperImport.LAYER_ORTHOGRAPHY+"::"+TCFDictionary.TAG_TC_CORRECTION);
			assertNotNull(anno);
			assertEquals(docNode.getSAnnotation(TCFMapperImport.LAYER_ORTHOGRAPHY+"::"+TCFDictionary.TAG_TC_CORRECTION).getValue(), anno.getValue());
			operation = (SAnnotation)anno.getLabel(TCFMapperImport.LAYER_ORTHOGRAPHY+"::"+TCFDictionary.ATT_OPERATION);
			assertNotNull(operation);
			assertEquals(((SAnnotation)docNode.getSAnnotation(TCFMapperImport.LAYER_ORTHOGRAPHY+"::"+TCFDictionary.TAG_TC_CORRECTION).getLabel(TCFMapperImport.LAYER_ORTHOGRAPHY+"::"+TCFDictionary.ATT_OPERATION)).getValue(), operation.getValue());
		}
	}

	/**This method tests if a valid TCF-XML-structure containing orthography
	 * annotations is converted to salt correctly
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testOrthographyNotShrinked() throws XMLStreamException, FileNotFoundException{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o= XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);
		
		xmlWriter.writeStartDocument();
		xmlWriter.writeProcessingInstruction(TCFDictionary.TCF_PI);
		xmlWriter.writeStartElement(TCFDictionary.NS_WL, TCFDictionary.TAG_WL_D_SPIN, TCFDictionary.NS_VALUE_WL);
		xmlWriter.writeNamespace(TCFDictionary.NS_WL, TCFDictionary.NS_VALUE_WL);
		xmlWriter.writeNamespace(TCFDictionary.NS_ED, TCFDictionary.NS_VALUE_ED);
		xmlWriter.writeNamespace(TCFDictionary.NS_LX, TCFDictionary.NS_VALUE_LX);
		xmlWriter.writeNamespace(TCFDictionary.NS_MD, TCFDictionary.NS_VALUE_MD);
		xmlWriter.writeNamespace(TCFDictionary.NS_TC, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_VERSION, "4.0");
			xmlWriter.writeStartElement(TCFDictionary.NS_MD, TCFDictionary.TAG_MD_METADATA, TCFDictionary.NS_VALUE_MD);
			xmlWriter.writeEndElement();
			xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTCORPUS, TCFDictionary.NS_VALUE_TC);
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXT, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeCharacters(EXAMPLE_TEXT_ORTHOGRAPHY);
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKENS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t1");
						xmlWriter.writeCharacters("Ei");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t2");
						xmlWriter.writeCharacters("laaf");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t3");
						xmlWriter.writeCharacters("Nuh");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t4");
						xmlWriter.writeCharacters("Jork");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t5");
						xmlWriter.writeCharacters(".");
					xmlWriter.writeEndElement();					
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ORTHOGRAPHY, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CORRECTION, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_OPERATION, "replace");
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t1");
					xmlWriter.writeCharacters("I");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CORRECTION, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_OPERATION, "replace");
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t2");
					xmlWriter.writeCharacters("love");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CORRECTION, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_OPERATION, "replace");
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t3 t4");
					xmlWriter.writeCharacters("New York");
					xmlWriter.writeEndElement();
				xmlWriter.writeEndElement();
			xmlWriter.writeEndElement();
		xmlWriter.writeEndElement();
		xmlWriter.writeEndDocument();
		
		/* generating salt sample */
		SDocument doc = SaltFactory.eINSTANCE.createSDocument();
		SDocumentGraph docGraph = SaltFactory.eINSTANCE.createSDocumentGraph();
		doc.setSDocumentGraph(docGraph);
		docGraph.createSTextualDS(EXAMPLE_TEXT_ORTHOGRAPHY);
		docGraph.tokenize();
		EList<SToken> docTokens = docGraph.getSTokens();
		SLayer orthLayer = SaltFactory.eINSTANCE.createSLayer();
		orthLayer.setSName(TCFMapperImport.LAYER_ORTHOGRAPHY);
		docGraph.addSLayer(orthLayer);
		EList<SNode> docOrthNodes = orthLayer.getSNodes();
		
		SAnnotation anno = null;
		SAnnotation operation = null;
		SSpan sSpan = null;
		
		sSpan = docGraph.createSSpan(docTokens.get(0));
		anno = sSpan.createSAnnotation(TCFMapperImport.LAYER_ORTHOGRAPHY, TCFDictionary.TAG_TC_CORRECTION, "I");
		operation = SaltFactory.eINSTANCE.createSAnnotation();
		operation.setNamespace(TCFMapperImport.LAYER_ORTHOGRAPHY);
		operation.setName(TCFDictionary.ATT_OPERATION);
		operation.setValue("replace");
		anno.addLabel(operation);
		docOrthNodes.add(sSpan);
		
		sSpan = docGraph.createSSpan(docTokens.get(1));
		anno = sSpan.createSAnnotation(TCFMapperImport.LAYER_ORTHOGRAPHY, TCFDictionary.TAG_TC_CORRECTION, "love");
		operation = SaltFactory.eINSTANCE.createSAnnotation();
		operation.setNamespace(TCFMapperImport.LAYER_ORTHOGRAPHY);
		operation.setName(TCFDictionary.ATT_OPERATION);
		operation.setValue("replace");
		anno.addLabel(operation);
		docOrthNodes.add(sSpan);
		
		sSpan = docGraph.createSSpan(docTokens.get(2));
		docGraph.addSNode(sSpan, docTokens.get(3), STYPE_NAME.SSPANNING_RELATION);
		anno = sSpan.createSAnnotation(TCFMapperImport.LAYER_ORTHOGRAPHY, TCFDictionary.TAG_TC_CORRECTION, "New York");		
		operation = SaltFactory.eINSTANCE.createSAnnotation();
		operation.setNamespace(TCFMapperImport.LAYER_ORTHOGRAPHY);
		operation.setName(TCFDictionary.ATT_OPERATION);
		operation.setValue("replace");
		anno.addLabel(operation);
		docOrthNodes.add(sSpan);
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_ORTHOGRAPHY);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		this.getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, false);
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, false);
		
		/* start mapper */
				
		this.getFixture().mapSDocument();
		
		
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = this.getFixture().getSDocument().getSDocumentGraph();		
		assertFalse(fixGraph.getSLayerByName(TCFMapperImport.LAYER_ORTHOGRAPHY).isEmpty());
		EList<SNode> fixOrthNodes = fixGraph.getSLayerByName(TCFMapperImport.LAYER_ORTHOGRAPHY).get(0).getSNodes();
		assertEquals(docOrthNodes.size(), fixOrthNodes.size());
		SNode docNode = null;
		SNode fixNode = null;
		for(int i=0; i<docOrthNodes.size(); i++){
			docNode = docOrthNodes.get(i);
			fixNode = fixOrthNodes.get(i);			
			assertTrue(fixNode instanceof SSpan);
			assertEquals(docGraph.getSText(docNode), fixGraph.getSText(fixNode));
			anno = fixNode.getSAnnotation(TCFMapperImport.LAYER_ORTHOGRAPHY+"::"+TCFDictionary.TAG_TC_CORRECTION);
			assertNotNull(anno);
			assertEquals(docNode.getSAnnotation(TCFMapperImport.LAYER_ORTHOGRAPHY+"::"+TCFDictionary.TAG_TC_CORRECTION).getValue(), anno.getValue());
			operation = (SAnnotation)anno.getLabel(TCFMapperImport.LAYER_ORTHOGRAPHY+"::"+TCFDictionary.ATT_OPERATION);
			assertNotNull(operation);
			assertEquals(((SAnnotation)docNode.getSAnnotation(TCFMapperImport.LAYER_ORTHOGRAPHY+"::"+TCFDictionary.TAG_TC_CORRECTION).getLabel(TCFMapperImport.LAYER_ORTHOGRAPHY+"::"+TCFDictionary.ATT_OPERATION)).getValue(), operation.getValue());
		}
	}
		
	/**This method tests if a valid TCF-XML-structure containing text structure
	 * annotations is converted to salt correctly 
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testGeographicalLocationsShrinked() throws XMLStreamException, FileNotFoundException{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o= XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);
		
		xmlWriter.writeStartDocument();
		xmlWriter.writeProcessingInstruction(TCFDictionary.TCF_PI);
		xmlWriter.writeStartElement(TCFDictionary.NS_WL, TCFDictionary.TAG_WL_D_SPIN, TCFDictionary.NS_VALUE_WL);
		xmlWriter.writeNamespace(TCFDictionary.NS_WL, TCFDictionary.NS_VALUE_WL);
		xmlWriter.writeNamespace(TCFDictionary.NS_ED, TCFDictionary.NS_VALUE_ED);
		xmlWriter.writeNamespace(TCFDictionary.NS_LX, TCFDictionary.NS_VALUE_LX);
		xmlWriter.writeNamespace(TCFDictionary.NS_MD, TCFDictionary.NS_VALUE_MD);
		xmlWriter.writeNamespace(TCFDictionary.NS_TC, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_VERSION, "0.4");
			xmlWriter.writeStartElement(TCFDictionary.NS_MD, TCFDictionary.TAG_MD_METADATA, TCFDictionary.NS_VALUE_MD);
			xmlWriter.writeEndElement();
			xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTCORPUS, TCFDictionary.NS_VALUE_TC);
			xmlWriter.writeAttribute(TCFDictionary.ATT_LANG, "en");
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXT, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeCharacters(EXAMPLE_TEXT_GEO);
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKENS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t1");
						xmlWriter.writeCharacters("New");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t2");
						xmlWriter.writeCharacters("York");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t3");
						xmlWriter.writeCharacters("is");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t4");
						xmlWriter.writeCharacters("not");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t5");
						xmlWriter.writeCharacters("Berlin");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t6");
						xmlWriter.writeCharacters(".");
					xmlWriter.writeEndElement();					
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_GEO, TCFDictionary.NS_VALUE_TC);
				xmlWriter.writeAttribute(TCFDictionary.ATT_COORDFORMAT, "DegDec");
				xmlWriter.writeAttribute(TCFDictionary.ATT_CONTINENTFORMAT, "name");
				xmlWriter.writeAttribute(TCFDictionary.ATT_COUNTRYFORMAT, "ISO3166_A2");
				xmlWriter.writeAttribute(TCFDictionary.ATT_CAPITALFORMAT, "name");
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SRC, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeCharacters("my fantasy");
					xmlWriter.writeEndElement();//end of src
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_GPOINT, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t1 t2");
					xmlWriter.writeAttribute(TCFDictionary.ATT_ALT, "1");
					xmlWriter.writeAttribute(TCFDictionary.ATT_LAT, "2");
					xmlWriter.writeAttribute(TCFDictionary.ATT_LON, "3");
					xmlWriter.writeAttribute(TCFDictionary.ATT_CONTINENT, "North America");
					xmlWriter.writeAttribute(TCFDictionary.ATT_COUNTRY, "U.S.A.");
					xmlWriter.writeAttribute(TCFDictionary.ATT_CAPITAL, "Washington (D.C.)");
					xmlWriter.writeEndElement();//end of gpoint
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_GPOINT, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t5");
					xmlWriter.writeAttribute(TCFDictionary.ATT_ALT, "2");
					xmlWriter.writeAttribute(TCFDictionary.ATT_LAT, "3");
					xmlWriter.writeAttribute(TCFDictionary.ATT_LON, "1");
					xmlWriter.writeAttribute(TCFDictionary.ATT_CONTINENT, "Europe");
					xmlWriter.writeAttribute(TCFDictionary.ATT_COUNTRY, "Germany");
					xmlWriter.writeAttribute(TCFDictionary.ATT_CAPITAL, "Berlin");
					xmlWriter.writeEndElement();//end of gpoint
				xmlWriter.writeEndElement();//end of geo
			xmlWriter.writeEndElement();
		xmlWriter.writeEndElement();
		xmlWriter.writeEndDocument();
		
		/* generating salt sample */
		SDocument doc = SaltFactory.eINSTANCE.createSDocument();
		SDocumentGraph docGraph = SaltFactory.eINSTANCE.createSDocumentGraph();
		doc.setSDocumentGraph(docGraph);
		docGraph.createSTextualDS(EXAMPLE_TEXT_ORTHOGRAPHY);
		docGraph.tokenize();
		EList<SToken> docTokens = docGraph.getSTokens();
		SLayer docGeoLayer = SaltFactory.eINSTANCE.createSLayer();
		docGeoLayer.setSName(TCFMapperImport.LAYER_GEO);
		docGraph.addSLayer(docGeoLayer);
		EList<SNode> docGeoNodes = docGeoLayer.getSNodes();
				
		SSpan newYork = docGraph.createSSpan(docTokens.get(1));
		docGraph.addSNode(newYork, docTokens.get(1), STYPE_NAME.SSPANNING_RELATION);
		newYork.createSAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_ALT, "1");
		newYork.createSAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_LAT, "2");
		newYork.createSAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_LON, "3");
		newYork.createSAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_CONTINENT, "North America");
		newYork.createSAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_COUNTRY, "U.S.A.");
		newYork.createSAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_CAPITAL, "Washington (D.C.)");
		docGeoNodes.add(newYork);

		docTokens.get(4).createSAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_ALT, "2");
		docTokens.get(4).createSAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_LAT, "3");
		docTokens.get(4).createSAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_LON, "1");
		docTokens.get(4).createSAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_CONTINENT, "Europe");
		docTokens.get(4).createSAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_COUNTRY, "Germany");
		docTokens.get(4).createSAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_CAPITAL, "Berlin");
		docGeoNodes.add(docTokens.get(4));
		
		docGeoLayer.createSMetaAnnotation(null, TCFDictionary.ATT_COORDFORMAT, "DegDec");
		docGeoLayer.createSMetaAnnotation(null, TCFDictionary.ATT_CONTINENTFORMAT, "name");
		docGeoLayer.createSMetaAnnotation(null, TCFDictionary.ATT_COUNTRYFORMAT, "ISO3166_A2");
		docGeoLayer.createSMetaAnnotation(null, TCFDictionary.ATT_CAPITALFORMAT, "name");
		docGeoLayer.createSMetaAnnotation(null, TCFDictionary.TAG_TC_SRC, "my fantasy");
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_GEO);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		this.getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, true);
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, false);
		
		/* start mapper */
				
		this.getFixture().mapSDocument();
		
		
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = this.getFixture().getSDocument().getSDocumentGraph();
		assertEquals(1, fixGraph.getSLayerByName(TCFMapperImport.LAYER_GEO).size());
		SLayer fixGeoLayer = fixGraph.getSLayerByName(TCFMapperImport.LAYER_GEO).get(0);
		EList<SNode> fixGeoNodes = fixGeoLayer.getSNodes();
		assertEquals(docGeoNodes.size(), fixGeoNodes.size());
		assertEquals(docGeoLayer.getSMetaAnnotations().size(), fixGeoLayer.getSMetaAnnotations().size());
		assertNotNull(fixGeoLayer.getSMetaAnnotation(TCFDictionary.ATT_COORDFORMAT));
		assertNotNull(fixGeoLayer.getSMetaAnnotation(TCFDictionary.ATT_CONTINENTFORMAT));
		assertNotNull(fixGeoLayer.getSMetaAnnotation(TCFDictionary.ATT_COUNTRYFORMAT));
		assertNotNull(fixGeoLayer.getSMetaAnnotation(TCFDictionary.ATT_CAPITALFORMAT));
		assertNotNull(fixGeoLayer.getSMetaAnnotation(TCFDictionary.TAG_TC_SRC));
		assertEquals(docGeoLayer.getSMetaAnnotation(TCFDictionary.ATT_COORDFORMAT).getValue(), fixGeoLayer.getSMetaAnnotation(TCFDictionary.ATT_COORDFORMAT).getValue());
		assertEquals(docGeoLayer.getSMetaAnnotation(TCFDictionary.ATT_CONTINENTFORMAT).getValue(), fixGeoLayer.getSMetaAnnotation(TCFDictionary.ATT_CONTINENTFORMAT).getValue());
		assertEquals(docGeoLayer.getSMetaAnnotation(TCFDictionary.ATT_COUNTRYFORMAT).getValue(), fixGeoLayer.getSMetaAnnotation(TCFDictionary.ATT_COUNTRYFORMAT).getValue());
		assertEquals(docGeoLayer.getSMetaAnnotation(TCFDictionary.ATT_CAPITALFORMAT).getValue(), fixGeoLayer.getSMetaAnnotation(TCFDictionary.ATT_CAPITALFORMAT).getValue());
		assertEquals(docGeoLayer.getSMetaAnnotation(TCFDictionary.TAG_TC_SRC).getValue(), fixGeoLayer.getSMetaAnnotation(TCFDictionary.TAG_TC_SRC).getValue());
		SNode docNode = null;
		SNode fixNode = null;
		for(int i=0; i<docGeoNodes.size(); i++){
			docNode = docGeoNodes.get(i);
			fixNode = fixGeoNodes.get(i);
			assertEquals(docNode.getClass(), fixNode.getClass());			
			assertEquals(docNode.getSAnnotation(TCFMapperImport.LAYER_GEO+"::"+TCFDictionary.ATT_ALT), fixNode.getSAnnotation(TCFMapperImport.LAYER_GEO+"::"+TCFDictionary.ATT_ALT));
			assertEquals(docNode.getSAnnotation(TCFMapperImport.LAYER_GEO+"::"+TCFDictionary.ATT_LAT), fixNode.getSAnnotation(TCFMapperImport.LAYER_GEO+"::"+TCFDictionary.ATT_LAT));
			assertEquals(docNode.getSAnnotation(TCFMapperImport.LAYER_GEO+"::"+TCFDictionary.ATT_LON), fixNode.getSAnnotation(TCFMapperImport.LAYER_GEO+"::"+TCFDictionary.ATT_LON));
			assertEquals(docNode.getSAnnotation(TCFMapperImport.LAYER_GEO+"::"+TCFDictionary.ATT_CONTINENT), fixNode.getSAnnotation(TCFMapperImport.LAYER_GEO+"::"+TCFDictionary.ATT_CONTINENT));
			assertEquals(docNode.getSAnnotation(TCFMapperImport.LAYER_GEO+"::"+TCFDictionary.ATT_COUNTRY), fixNode.getSAnnotation(TCFMapperImport.LAYER_GEO+"::"+TCFDictionary.ATT_COUNTRY));
			assertEquals(docNode.getSAnnotation(TCFMapperImport.LAYER_GEO+"::"+TCFDictionary.ATT_CAPITAL), fixNode.getSAnnotation(TCFMapperImport.LAYER_GEO+"::"+TCFDictionary.ATT_CAPITAL));
		}
	}
	
	/**This method tests if a valid TCF-XML-structure containing text structure
	 * annotations is converted to salt correctly 
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testGeographicalLocationsNotShrinked() throws XMLStreamException, FileNotFoundException{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o= XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);
		
		xmlWriter.writeStartDocument();
		xmlWriter.writeProcessingInstruction(TCFDictionary.TCF_PI);
		xmlWriter.writeStartElement(TCFDictionary.NS_WL, TCFDictionary.TAG_WL_D_SPIN, TCFDictionary.NS_VALUE_WL);
		xmlWriter.writeNamespace(TCFDictionary.NS_WL, TCFDictionary.NS_VALUE_WL);
		xmlWriter.writeNamespace(TCFDictionary.NS_ED, TCFDictionary.NS_VALUE_ED);
		xmlWriter.writeNamespace(TCFDictionary.NS_LX, TCFDictionary.NS_VALUE_LX);
		xmlWriter.writeNamespace(TCFDictionary.NS_MD, TCFDictionary.NS_VALUE_MD);
		xmlWriter.writeNamespace(TCFDictionary.NS_TC, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_VERSION, "0.4");
			xmlWriter.writeStartElement(TCFDictionary.NS_MD, TCFDictionary.TAG_MD_METADATA, TCFDictionary.NS_VALUE_MD);
			xmlWriter.writeEndElement();
			xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTCORPUS, TCFDictionary.NS_VALUE_TC);
			xmlWriter.writeAttribute(TCFDictionary.ATT_LANG, "en");
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXT, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeCharacters(EXAMPLE_TEXT_GEO);
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKENS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t1");
						xmlWriter.writeCharacters("New");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t2");
						xmlWriter.writeCharacters("York");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t3");
						xmlWriter.writeCharacters("is");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t4");
						xmlWriter.writeCharacters("not");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t5");
						xmlWriter.writeCharacters("Berlin");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t6");
						xmlWriter.writeCharacters(".");
					xmlWriter.writeEndElement();					
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_GEO, TCFDictionary.NS_VALUE_TC);
				xmlWriter.writeAttribute(TCFDictionary.ATT_COORDFORMAT, "DegDec");
				xmlWriter.writeAttribute(TCFDictionary.ATT_CONTINENTFORMAT, "name");
				xmlWriter.writeAttribute(TCFDictionary.ATT_COUNTRYFORMAT, "ISO3166_A2");
				xmlWriter.writeAttribute(TCFDictionary.ATT_CAPITALFORMAT, "name");
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SRC, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeCharacters("my fantasy");
					xmlWriter.writeEndElement();//end of src
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_GPOINT, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t1 t2");
					xmlWriter.writeAttribute(TCFDictionary.ATT_ALT, "1");
					xmlWriter.writeAttribute(TCFDictionary.ATT_LAT, "2");
					xmlWriter.writeAttribute(TCFDictionary.ATT_LON, "3");
					xmlWriter.writeAttribute(TCFDictionary.ATT_CONTINENT, "North America");
					xmlWriter.writeAttribute(TCFDictionary.ATT_COUNTRY, "U.S.A.");
					xmlWriter.writeAttribute(TCFDictionary.ATT_CAPITAL, "Washington (D.C.)");
					xmlWriter.writeEndElement();//end of gpoint
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_GPOINT, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t5");
					xmlWriter.writeAttribute(TCFDictionary.ATT_ALT, "2");
					xmlWriter.writeAttribute(TCFDictionary.ATT_LAT, "3");
					xmlWriter.writeAttribute(TCFDictionary.ATT_LON, "1");
					xmlWriter.writeAttribute(TCFDictionary.ATT_CONTINENT, "Europe");
					xmlWriter.writeAttribute(TCFDictionary.ATT_COUNTRY, "Germany");
					xmlWriter.writeAttribute(TCFDictionary.ATT_CAPITAL, "Berlin");
					xmlWriter.writeEndElement();//end of gpoint
				xmlWriter.writeEndElement();//end of geo
			xmlWriter.writeEndElement();
		xmlWriter.writeEndElement();
		xmlWriter.writeEndDocument();
		
		/* generating salt sample */
		SDocument doc = SaltFactory.eINSTANCE.createSDocument();
		SDocumentGraph docGraph = SaltFactory.eINSTANCE.createSDocumentGraph();
		doc.setSDocumentGraph(docGraph);
		docGraph.createSTextualDS(EXAMPLE_TEXT_ORTHOGRAPHY);
		docGraph.tokenize();
		EList<SToken> docTokens = docGraph.getSTokens();
		SLayer docGeoLayer = SaltFactory.eINSTANCE.createSLayer();
		docGeoLayer.setSName(TCFMapperImport.LAYER_GEO);
		docGraph.addSLayer(docGeoLayer);
		EList<SNode> docGeoNodes = docGeoLayer.getSNodes();
				
		SSpan newYork = docGraph.createSSpan(docTokens.get(1));
		docGraph.addSNode(newYork, docTokens.get(1), STYPE_NAME.SSPANNING_RELATION);
		newYork.createSAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_ALT, "1");
		newYork.createSAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_LAT, "2");
		newYork.createSAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_LON, "3");
		newYork.createSAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_CONTINENT, "North America");
		newYork.createSAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_COUNTRY, "U.S.A.");
		newYork.createSAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_CAPITAL, "Washington (D.C.)");
		docGeoNodes.add(newYork);

		SSpan berlin = docGraph.createSSpan(docTokens.get(4));
		berlin.createSAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_ALT, "2");
		berlin.createSAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_LAT, "3");
		berlin.createSAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_LON, "1");
		berlin.createSAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_CONTINENT, "Europe");
		berlin.createSAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_COUNTRY, "Germany");
		berlin.createSAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_CAPITAL, "Berlin");
		docGeoNodes.add(berlin);
		
		docGeoLayer.createSMetaAnnotation(null, TCFDictionary.ATT_COORDFORMAT, "DegDec");
		docGeoLayer.createSMetaAnnotation(null, TCFDictionary.ATT_CONTINENTFORMAT, "name");
		docGeoLayer.createSMetaAnnotation(null, TCFDictionary.ATT_COUNTRYFORMAT, "ISO3166_A2");
		docGeoLayer.createSMetaAnnotation(null, TCFDictionary.ATT_CAPITALFORMAT, "name");
		docGeoLayer.createSMetaAnnotation(null, TCFDictionary.TAG_TC_SRC, "my fantasy");
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_GEO);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		this.getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, false);
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, false);
		
		/* start mapper */
				
		this.getFixture().mapSDocument();
		
		
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = this.getFixture().getSDocument().getSDocumentGraph();
		assertEquals(1, fixGraph.getSLayerByName(TCFMapperImport.LAYER_GEO).size());
		SLayer fixGeoLayer = fixGraph.getSLayerByName(TCFMapperImport.LAYER_GEO).get(0);
		EList<SNode> fixGeoNodes = fixGeoLayer.getSNodes();
		assertEquals(docGeoNodes.size(), fixGeoNodes.size());
		assertEquals(docGeoLayer.getSMetaAnnotations().size(), fixGeoLayer.getSMetaAnnotations().size());
		assertNotNull(fixGeoLayer.getSMetaAnnotation(TCFDictionary.ATT_COORDFORMAT));
		assertNotNull(fixGeoLayer.getSMetaAnnotation(TCFDictionary.ATT_CONTINENTFORMAT));
		assertNotNull(fixGeoLayer.getSMetaAnnotation(TCFDictionary.ATT_COUNTRYFORMAT));
		assertNotNull(fixGeoLayer.getSMetaAnnotation(TCFDictionary.ATT_CAPITALFORMAT));
		assertNotNull(fixGeoLayer.getSMetaAnnotation(TCFDictionary.TAG_TC_SRC));
		assertEquals(docGeoLayer.getSMetaAnnotation(TCFDictionary.ATT_COORDFORMAT).getValue(), fixGeoLayer.getSMetaAnnotation(TCFDictionary.ATT_COORDFORMAT).getValue());
		assertEquals(docGeoLayer.getSMetaAnnotation(TCFDictionary.ATT_CONTINENTFORMAT).getValue(), fixGeoLayer.getSMetaAnnotation(TCFDictionary.ATT_CONTINENTFORMAT).getValue());
		assertEquals(docGeoLayer.getSMetaAnnotation(TCFDictionary.ATT_COUNTRYFORMAT).getValue(), fixGeoLayer.getSMetaAnnotation(TCFDictionary.ATT_COUNTRYFORMAT).getValue());
		assertEquals(docGeoLayer.getSMetaAnnotation(TCFDictionary.ATT_CAPITALFORMAT).getValue(), fixGeoLayer.getSMetaAnnotation(TCFDictionary.ATT_CAPITALFORMAT).getValue());
		assertEquals(docGeoLayer.getSMetaAnnotation(TCFDictionary.TAG_TC_SRC).getValue(), fixGeoLayer.getSMetaAnnotation(TCFDictionary.TAG_TC_SRC).getValue());
		SNode docNode = null;
		SNode fixNode = null;
		for(int i=0; i<docGeoNodes.size(); i++){
			docNode = docGeoNodes.get(i);
			fixNode = fixGeoNodes.get(i);
			assertTrue(fixNode instanceof SSpan);			
			assertEquals(docNode.getSAnnotation(TCFMapperImport.LAYER_GEO+"::"+TCFDictionary.ATT_ALT), fixNode.getSAnnotation(TCFMapperImport.LAYER_GEO+"::"+TCFDictionary.ATT_ALT));
			assertEquals(docNode.getSAnnotation(TCFMapperImport.LAYER_GEO+"::"+TCFDictionary.ATT_LAT), fixNode.getSAnnotation(TCFMapperImport.LAYER_GEO+"::"+TCFDictionary.ATT_LAT));
			assertEquals(docNode.getSAnnotation(TCFMapperImport.LAYER_GEO+"::"+TCFDictionary.ATT_LON), fixNode.getSAnnotation(TCFMapperImport.LAYER_GEO+"::"+TCFDictionary.ATT_LON));
			assertEquals(docNode.getSAnnotation(TCFMapperImport.LAYER_GEO+"::"+TCFDictionary.ATT_CONTINENT), fixNode.getSAnnotation(TCFMapperImport.LAYER_GEO+"::"+TCFDictionary.ATT_CONTINENT));
			assertEquals(docNode.getSAnnotation(TCFMapperImport.LAYER_GEO+"::"+TCFDictionary.ATT_COUNTRY), fixNode.getSAnnotation(TCFMapperImport.LAYER_GEO+"::"+TCFDictionary.ATT_COUNTRY));
			assertEquals(docNode.getSAnnotation(TCFMapperImport.LAYER_GEO+"::"+TCFDictionary.ATT_CAPITAL), fixNode.getSAnnotation(TCFMapperImport.LAYER_GEO+"::"+TCFDictionary.ATT_CAPITAL));
		}
	}
	
	/**This method tests if a valid TCF-XML-structure containing lexical-semantic
	 * annotations is converted to salt correctly 
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testLexicalSemanticAnnotationsShrinked() throws XMLStreamException, FileNotFoundException{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o= XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);
		
		xmlWriter.writeStartDocument();
		xmlWriter.writeProcessingInstruction(TCFDictionary.TCF_PI);
			xmlWriter.writeStartElement(TCFDictionary.NS_WL, TCFDictionary.TAG_WL_D_SPIN, TCFDictionary.NS_VALUE_WL);
			xmlWriter.writeNamespace(TCFDictionary.NS_WL, TCFDictionary.NS_VALUE_WL);
			xmlWriter.writeNamespace(TCFDictionary.NS_ED, TCFDictionary.NS_VALUE_ED);
			xmlWriter.writeNamespace(TCFDictionary.NS_LX, TCFDictionary.NS_VALUE_LX);
			xmlWriter.writeNamespace(TCFDictionary.NS_MD, TCFDictionary.NS_VALUE_MD);
			xmlWriter.writeNamespace(TCFDictionary.NS_TC, TCFDictionary.NS_VALUE_TC);
			xmlWriter.writeAttribute(TCFDictionary.ATT_VERSION, "0.4");
				xmlWriter.writeStartElement(TCFDictionary.NS_MD, TCFDictionary.TAG_MD_METADATA, TCFDictionary.NS_VALUE_MD);
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTCORPUS, TCFDictionary.NS_VALUE_TC);
				xmlWriter.writeAttribute(TCFDictionary.ATT_LANG, "de");
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXT, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeCharacters(EXAMPLE_TEXT_SHRINK);
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKENS, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t1");
							xmlWriter.writeCharacters("I");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t2");
							xmlWriter.writeCharacters("love");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t3");
							xmlWriter.writeCharacters("New");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t4");
							xmlWriter.writeCharacters("York");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t5");
							xmlWriter.writeCharacters(".");
						xmlWriter.writeEndElement();						
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_LEMMAS, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_LEMMA, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "le1");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t1");
						xmlWriter.writeCharacters("I");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_LEMMA, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "le2");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t2");
						xmlWriter.writeCharacters("love");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_LEMMA, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "le3");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t3 t4");
						xmlWriter.writeCharacters("New York");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_LEMMA, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "le4");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t5");
						xmlWriter.writeCharacters(".");
						xmlWriter.writeEndElement();						
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SYNONYMY, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ORTHFORM, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_LEMMAREFS, "le2");
							xmlWriter.writeCharacters("admire, like");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ORTHFORM, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_LEMMAREFS, "le3");
							xmlWriter.writeCharacters("N.Y., Big Apple");
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();//end of synonymy
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ANTONYMY, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ORTHFORM, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_LEMMAREFS, "le1");
							xmlWriter.writeCharacters("the set of human/animate entities in this world not including me");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ORTHFORM, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_LEMMAREFS, "le2");
							xmlWriter.writeCharacters("hate, dislike");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ORTHFORM, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_LEMMAREFS, "le3");
							xmlWriter.writeCharacters("the set of places not including New York");
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();//end of antonymy
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_HYPONYMY, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ORTHFORM, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_LEMMAREFS, "le1");
							xmlWriter.writeCharacters("you, he, she, it, we, you, they");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ORTHFORM, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_LEMMAREFS, "le2");
							xmlWriter.writeCharacters("hate, dislike, fear, appreciate, ...");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ORTHFORM, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_LEMMAREFS, "le3");
							xmlWriter.writeCharacters("Schweinfurth, Graz, Cannes, Manchester, ...");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ORTHFORM, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_LEMMAREFS, "le4");
							xmlWriter.writeCharacters("!, ?");
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();//end of hyponymy
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_HYPERONYMY, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ORTHFORM, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_LEMMAREFS, "le1");
							xmlWriter.writeCharacters("PPERs");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ORTHFORM, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_LEMMAREFS, "le2");
							xmlWriter.writeCharacters("verbs of experience");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ORTHFORM, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_LEMMAREFS, "le3");
							xmlWriter.writeCharacters("cities");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ORTHFORM, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_LEMMAREFS, "le4");
							xmlWriter.writeCharacters("sentence final characters");
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();//end of hyperonymy
				xmlWriter.writeEndElement();//End of TextCorpus
			xmlWriter.writeEndElement();
		xmlWriter.writeEndDocument();
		
		/* generating salt sample */
		SDocument doc = SaltFactory.eINSTANCE.createSDocument();
		SDocumentGraph docGraph = SaltFactory.eINSTANCE.createSDocumentGraph();
		doc.setSDocumentGraph(docGraph);
		docGraph.createSTextualDS(EXAMPLE_TEXT_SHRINK);
		docGraph.tokenize();
		EList<SToken> docTokens = docGraph.getSTokens();
		SLayer docLexLayer = SaltFactory.eINSTANCE.createSLayer();
		docLexLayer.setSName(TCFMapperImport.LAYER_LS);
		docGraph.addSLayer(docLexLayer);
		EList<SNode> docLexNodes = docLexLayer.getSNodes();

		SAnnotation synonym = SaltFactory.eINSTANCE.createSAnnotation();
		synonym.setNamespace(TCFMapperImport.LAYER_LS);
		synonym.setName(TCFDictionary.TAG_TC_SYNONYMY);	
		
		SAnnotation antonym = SaltFactory.eINSTANCE.createSAnnotation();
		antonym.setNamespace(TCFMapperImport.LAYER_LS);
		antonym.setName(TCFDictionary.TAG_TC_ANTONYMY);
		
		SAnnotation hyponym = SaltFactory.eINSTANCE.createSAnnotation();
		hyponym.setNamespace(TCFMapperImport.LAYER_LS);
		hyponym.setName(TCFDictionary.TAG_TC_HYPONYMY);
		
		SAnnotation hyperonym = SaltFactory.eINSTANCE.createSAnnotation();
		hyperonym.setNamespace(TCFMapperImport.LAYER_LS);
		hyperonym.setName(TCFDictionary.TAG_TC_HYPERONYMY);
		
		SAnnotation docLemma = SaltFactory.eINSTANCE.createSLemmaAnnotation();
		docLemma.setValue("I");
		docTokens.get(0).addSAnnotation(docLemma);
		antonym.setValue("the set of human/animate entities in this world not including me");
		hyponym.setValue("you, he, she, it, we, you, they");
		hyperonym.setValue("PPERs");
		docLemma.addLabel(antonym);
		docLemma.addLabel(hyponym);
		docLemma.addLabel(hyperonym);		
		
		docLemma = SaltFactory.eINSTANCE.createSLemmaAnnotation();
		docLemma.setValue("love");
		docTokens.get(1).addSAnnotation(docLemma);
		antonym = (SAnnotation)antonym.clone();
		hyponym = (SAnnotation)hyponym.clone();
		hyperonym = (SAnnotation)hyperonym.clone();
		synonym.setValue("admire, like");		
		antonym.setValue("hate, dislike");
		hyponym.setValue("hate, dislike, fear, appreciate, ...");
		hyperonym.setValue("verbs of experience");
		docLemma.addLabel(synonym);
		docLemma.addLabel(antonym);
		docLemma.addLabel(hyponym);
		docLemma.addLabel(hyperonym);
		
		docLemma = SaltFactory.eINSTANCE.createSLemmaAnnotation();
		docLemma.setValue("New York"); 
		SSpan newYork = docGraph.createSSpan(docTokens.get(2));
		docGraph.addSNode(newYork, docTokens.get(3), STYPE_NAME.SSPANNING_RELATION);
		newYork.addSAnnotation(docLemma);
		synonym = (SAnnotation)synonym.clone();
		antonym = (SAnnotation)antonym.clone();
		hyponym = (SAnnotation)hyponym.clone();
		hyperonym = (SAnnotation)hyperonym.clone();
		synonym.setValue("N.Y., Big Apple");		
		antonym.setValue("the set of places not including New York");
		hyponym.setValue("Schweinfurth, Graz, Cannes, Manchester, ...");
		hyperonym.setValue("cities");
		docLemma.addLabel(synonym);
		docLemma.addLabel(antonym);
		docLemma.addLabel(hyponym);
		docLemma.addLabel(hyperonym);
		
		docLemma = SaltFactory.eINSTANCE.createSLemmaAnnotation();
		docLemma.setValue(".");
		docTokens.get(4).addSAnnotation(docLemma);
		hyponym = (SAnnotation)hyponym.clone();
		hyperonym = (SAnnotation)hyperonym.clone();
		hyponym.setValue("!, ?");
		hyperonym.setValue("sentence final characters");
		docLemma.addLabel(hyponym);
		docLemma.addLabel(hyperonym);
				
		/* since the parser first hits synonymy and token 0 and 4 (1 and 5) are not contained, we must add them in order of their appearance */
		docLexNodes.add(docTokens.get(1));
		docLexNodes.add(newYork);
		docLexNodes.add(docTokens.get(0));
		docLexNodes.add(docTokens.get(4));
				
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_LEXICALSEMANTICS);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		this.getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, true);
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, false);
		
		/* start mapper */
				
		this.getFixture().mapSDocument();
		
		
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = this.getFixture().getSDocument().getSDocumentGraph();
		assertEquals(1, fixGraph.getSLayerByName(TCFMapperImport.LAYER_LS).size());
		SLayer fixLexLayer = fixGraph.getSLayerByName(TCFMapperImport.LAYER_LS).get(0);
		EList<SNode> fixLexNodes = fixLexLayer.getSNodes();
		assertEquals(docLexNodes.size(), fixLexNodes.size());
		SNode docNode = null;
		SNode fixNode = null;
		SLemmaAnnotation fixLemma = null;		
		String lemmaQName = SaltSemanticsPackage.eNS_PREFIX+"::"+SALT_SEMANTIC_NAMES.LEMMA.toString();
		for(int i=0; i<docLexNodes.size(); i++){
			if(DEBUG){}
			docNode = docLexNodes.get(i);
			fixNode = fixLexNodes.get(i);
			assertEquals(docNode.getClass(), fixNode.getClass());
			assertEquals(docGraph.getSText(docNode), fixGraph.getSText(fixNode));
			docLemma = docNode.getSAnnotation(lemmaQName);
			fixLemma = (SLemmaAnnotation) fixNode.getSAnnotation(lemmaQName);
			assertNotNull(fixLemma);
			assertEquals(docLemma, fixLemma);			
		}
	}

	/**This method tests if a valid TCF-XML-structure containing lexical-semantic
	 * annotations is converted to salt correctly 
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testLexicalSemanticAnnotationsNotShrinked() throws XMLStreamException, FileNotFoundException{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o= XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);
		
		xmlWriter.writeStartDocument();
		xmlWriter.writeProcessingInstruction(TCFDictionary.TCF_PI);
			xmlWriter.writeStartElement(TCFDictionary.NS_WL, TCFDictionary.TAG_WL_D_SPIN, TCFDictionary.NS_VALUE_WL);
			xmlWriter.writeNamespace(TCFDictionary.NS_WL, TCFDictionary.NS_VALUE_WL);
			xmlWriter.writeNamespace(TCFDictionary.NS_ED, TCFDictionary.NS_VALUE_ED);
			xmlWriter.writeNamespace(TCFDictionary.NS_LX, TCFDictionary.NS_VALUE_LX);
			xmlWriter.writeNamespace(TCFDictionary.NS_MD, TCFDictionary.NS_VALUE_MD);
			xmlWriter.writeNamespace(TCFDictionary.NS_TC, TCFDictionary.NS_VALUE_TC);
			xmlWriter.writeAttribute(TCFDictionary.ATT_VERSION, "0.4");
				xmlWriter.writeStartElement(TCFDictionary.NS_MD, TCFDictionary.TAG_MD_METADATA, TCFDictionary.NS_VALUE_MD);
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTCORPUS, TCFDictionary.NS_VALUE_TC);
				xmlWriter.writeAttribute(TCFDictionary.ATT_LANG, "de");
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXT, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeCharacters(EXAMPLE_TEXT_SHRINK);
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKENS, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t1");
							xmlWriter.writeCharacters("I");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t2");
							xmlWriter.writeCharacters("love");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t3");
							xmlWriter.writeCharacters("New");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t4");
							xmlWriter.writeCharacters("York");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t5");
							xmlWriter.writeCharacters(".");
						xmlWriter.writeEndElement();						
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_LEMMAS, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_LEMMA, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "le1");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t1");
						xmlWriter.writeCharacters("I");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_LEMMA, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "le2");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t2");
						xmlWriter.writeCharacters("love");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_LEMMA, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "le3");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t3 t4");
						xmlWriter.writeCharacters("New York");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_LEMMA, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "le4");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t5");
						xmlWriter.writeCharacters(".");
						xmlWriter.writeEndElement();						
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SYNONYMY, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ORTHFORM, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_LEMMAREFS, "le2");
							xmlWriter.writeCharacters("admire, like");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ORTHFORM, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_LEMMAREFS, "le3");
							xmlWriter.writeCharacters("N.Y., Big Apple");
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();//end of synonymy
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ANTONYMY, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ORTHFORM, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_LEMMAREFS, "le1");
							xmlWriter.writeCharacters("the set of human/animate entities in this world not including me");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ORTHFORM, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_LEMMAREFS, "le2");
							xmlWriter.writeCharacters("hate, dislike");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ORTHFORM, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_LEMMAREFS, "le3");
							xmlWriter.writeCharacters("the set of places not including New York");
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();//end of antonymy
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_HYPONYMY, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ORTHFORM, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_LEMMAREFS, "le1");
							xmlWriter.writeCharacters("you, he, she, it, we, you, they");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ORTHFORM, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_LEMMAREFS, "le2");
							xmlWriter.writeCharacters("hate, dislike, fear, appreciate, ...");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ORTHFORM, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_LEMMAREFS, "le3");
							xmlWriter.writeCharacters("Schweinfurth, Graz, Cannes, Manchester, ...");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ORTHFORM, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_LEMMAREFS, "le4");
							xmlWriter.writeCharacters("!, ?");
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();//end of hyponymy
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_HYPERONYMY, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ORTHFORM, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_LEMMAREFS, "le1");
							xmlWriter.writeCharacters("PPERs");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ORTHFORM, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_LEMMAREFS, "le2");
							xmlWriter.writeCharacters("verbs of experience");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ORTHFORM, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_LEMMAREFS, "le3");
							xmlWriter.writeCharacters("cities");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_ORTHFORM, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_LEMMAREFS, "le4");
							xmlWriter.writeCharacters("sentence final characters");
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();//end of hyperonymy
				xmlWriter.writeEndElement();//End of TextCorpus
			xmlWriter.writeEndElement();
		xmlWriter.writeEndDocument();
		
		/* generating salt sample */
		SDocument doc = SaltFactory.eINSTANCE.createSDocument();
		SDocumentGraph docGraph = SaltFactory.eINSTANCE.createSDocumentGraph();
		doc.setSDocumentGraph(docGraph);
		docGraph.createSTextualDS(EXAMPLE_TEXT_SHRINK);
		docGraph.tokenize();
		EList<SToken> docTokens = docGraph.getSTokens();
		SLayer docLexLayer = SaltFactory.eINSTANCE.createSLayer();
		docLexLayer.setSName(TCFMapperImport.LAYER_LS);
		docGraph.addSLayer(docLexLayer);
		EList<SNode> docLexNodes = docLexLayer.getSNodes();

		SAnnotation synonym = SaltFactory.eINSTANCE.createSAnnotation();
		synonym.setNamespace(TCFMapperImport.LAYER_LS);
		synonym.setName(TCFDictionary.TAG_TC_SYNONYMY);	
		
		SAnnotation antonym = SaltFactory.eINSTANCE.createSAnnotation();
		antonym.setNamespace(TCFMapperImport.LAYER_LS);
		antonym.setName(TCFDictionary.TAG_TC_ANTONYMY);
		
		SAnnotation hyponym = SaltFactory.eINSTANCE.createSAnnotation();
		hyponym.setNamespace(TCFMapperImport.LAYER_LS);
		hyponym.setName(TCFDictionary.TAG_TC_HYPONYMY);
		
		SAnnotation hyperonym = SaltFactory.eINSTANCE.createSAnnotation();
		hyperonym.setNamespace(TCFMapperImport.LAYER_LS);
		hyperonym.setName(TCFDictionary.TAG_TC_HYPERONYMY);
		
		SAnnotation docLemma = SaltFactory.eINSTANCE.createSLemmaAnnotation();
		docLemma.setValue("I");
		docGraph.createSSpan(docTokens.get(0)).addSAnnotation(docLemma);
		antonym.setValue("the set of human/animate entities in this world not including me");
		hyponym.setValue("you, he, she, it, we, you, they");
		hyperonym.setValue("PPERs");
		docLemma.addLabel(antonym);
		docLemma.addLabel(hyponym);
		docLemma.addLabel(hyperonym);		
		
		docLemma = SaltFactory.eINSTANCE.createSLemmaAnnotation();
		docLemma.setValue("love");
		docGraph.createSSpan(docTokens.get(1)).addSAnnotation(docLemma);
		antonym = (SAnnotation)antonym.clone();
		hyponym = (SAnnotation)hyponym.clone();
		hyperonym = (SAnnotation)hyperonym.clone();
		synonym.setValue("admire, like");		
		antonym.setValue("hate, dislike");
		hyponym.setValue("hate, dislike, fear, appreciate, ...");
		hyperonym.setValue("verbs of experience");
		docLemma.addLabel(synonym);
		docLemma.addLabel(antonym);
		docLemma.addLabel(hyponym);
		docLemma.addLabel(hyperonym);
		
		docLemma = SaltFactory.eINSTANCE.createSLemmaAnnotation();
		docLemma.setValue("New York"); 
		SSpan newYork = docGraph.createSSpan(docTokens.get(2));
		docGraph.addSNode(newYork, docTokens.get(3), STYPE_NAME.SSPANNING_RELATION);
		newYork.addSAnnotation(docLemma);
		synonym = (SAnnotation)synonym.clone();
		antonym = (SAnnotation)antonym.clone();
		hyponym = (SAnnotation)hyponym.clone();
		hyperonym = (SAnnotation)hyperonym.clone();
		synonym.setValue("N.Y., Big Apple");		
		antonym.setValue("the set of places not including New York");
		hyponym.setValue("Schweinfurth, Graz, Cannes, Manchester, ...");
		hyperonym.setValue("cities");
		docLemma.addLabel(synonym);
		docLemma.addLabel(antonym);
		docLemma.addLabel(hyponym);
		docLemma.addLabel(hyperonym);
		
		docLemma = SaltFactory.eINSTANCE.createSLemmaAnnotation();
		docLemma.setValue(".");
		docGraph.createSSpan(docTokens.get(4)).addSAnnotation(docLemma);
		hyponym = (SAnnotation)hyponym.clone();
		hyperonym = (SAnnotation)hyperonym.clone();
		hyponym.setValue("!, ?");
		hyperonym.setValue("sentence final characters");
		docLemma.addLabel(hyponym);
		docLemma.addLabel(hyperonym);
				
		/* since the parser first hits synonymy and token 0 and 4 (1 and 5) are not contained, we must add them in order of their appearance */
		docLexNodes.add(docGraph.getSSpans().get(1));
		docLexNodes.add(newYork);
		docLexNodes.add(docGraph.getSSpans().get(0));
		docLexNodes.add(docGraph.getSSpans().get(3));
				
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_LEXICALSEMANTICS);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		this.getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, false);
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, false);
		
		/* start mapper */
				
		this.getFixture().mapSDocument();
		
		
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = this.getFixture().getSDocument().getSDocumentGraph();
		assertEquals(1, fixGraph.getSLayerByName(TCFMapperImport.LAYER_LS).size());
		SLayer fixLexLayer = fixGraph.getSLayerByName(TCFMapperImport.LAYER_LS).get(0);
		EList<SNode> fixLexNodes = fixLexLayer.getSNodes();
		assertEquals(docLexNodes.size(), fixLexNodes.size());
		SNode docNode = null;
		SNode fixNode = null;
		SLemmaAnnotation fixLemma = null;		
		String lemmaQName = SaltSemanticsPackage.eNS_PREFIX+"::"+SALT_SEMANTIC_NAMES.LEMMA.toString();
		for(int i=0; i<docLexNodes.size(); i++){
			if(DEBUG){}
			docNode = docLexNodes.get(i);
			fixNode = fixLexNodes.get(i);
			assertTrue(fixNode instanceof SSpan);
			assertEquals(docGraph.getSText(docNode), fixGraph.getSText(fixNode));
			docLemma = docNode.getSAnnotation(lemmaQName);
			fixLemma = (SLemmaAnnotation) fixNode.getSAnnotation(lemmaQName);
			assertNotNull(fixLemma);
			assertEquals(docLemma, fixLemma);			
		}
	}

	/**This method tests if a valid TCF-XML-structure containing lexical-semantic
	 * annotations is converted to salt correctly 
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testWordSenseDisambiguationShrinked() throws XMLStreamException, FileNotFoundException{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o= XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);
		
		xmlWriter.writeStartDocument();
		xmlWriter.writeProcessingInstruction(TCFDictionary.TCF_PI);
			xmlWriter.writeStartElement(TCFDictionary.NS_WL, TCFDictionary.TAG_WL_D_SPIN, TCFDictionary.NS_VALUE_WL);
			xmlWriter.writeNamespace(TCFDictionary.NS_WL, TCFDictionary.NS_VALUE_WL);
			xmlWriter.writeNamespace(TCFDictionary.NS_ED, TCFDictionary.NS_VALUE_ED);
			xmlWriter.writeNamespace(TCFDictionary.NS_LX, TCFDictionary.NS_VALUE_LX);
			xmlWriter.writeNamespace(TCFDictionary.NS_MD, TCFDictionary.NS_VALUE_MD);
			xmlWriter.writeNamespace(TCFDictionary.NS_TC, TCFDictionary.NS_VALUE_TC);
			xmlWriter.writeAttribute(TCFDictionary.ATT_VERSION, "0.4");
				xmlWriter.writeStartElement(TCFDictionary.NS_MD, TCFDictionary.TAG_MD_METADATA, TCFDictionary.NS_VALUE_MD);
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTCORPUS, TCFDictionary.NS_VALUE_TC);
				xmlWriter.writeAttribute(TCFDictionary.ATT_LANG, "de");
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXT, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeCharacters(EXAMPLE_TEXT_SHRINK);
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKENS, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t1");
							xmlWriter.writeCharacters("I");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t2");
							xmlWriter.writeCharacters("love");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t3");
							xmlWriter.writeCharacters("New");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t4");
							xmlWriter.writeCharacters("York");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t5");
							xmlWriter.writeCharacters(".");
						xmlWriter.writeEndElement();						
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_WSD, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_SRC, "any source");
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_WS, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t2");
						xmlWriter.writeAttribute(TCFDictionary.ATT_LEXUNITS, "14");
						xmlWriter.writeAttribute(TCFDictionary.ATT_COMMENT, "C.E.");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_WS, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t3 t4");
						xmlWriter.writeAttribute(TCFDictionary.ATT_LEXUNITS, "1 2 3");
						xmlWriter.writeAttribute(TCFDictionary.ATT_COMMENT, "from there to Germany");
						xmlWriter.writeCharacters("");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_WS, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t5");
						xmlWriter.writeAttribute(TCFDictionary.ATT_LEXUNITS, "0");
						xmlWriter.writeCharacters("");
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();//end of wsd
				xmlWriter.writeEndElement();//End of TextCorpus
			xmlWriter.writeEndElement();
		xmlWriter.writeEndDocument();
		
		/* generating salt sample */
		SDocument doc = SaltFactory.eINSTANCE.createSDocument();
		SDocumentGraph docGraph = SaltFactory.eINSTANCE.createSDocumentGraph();
		doc.setSDocumentGraph(docGraph);
		docGraph.createSTextualDS(EXAMPLE_TEXT_SHRINK);
		docGraph.tokenize();
		EList<SToken> docTokens = docGraph.getSTokens();
		SLayer docWSLayer = SaltFactory.eINSTANCE.createSLayer();
		docWSLayer.setSName(TCFMapperImport.LAYER_WORDSENSE);
		docWSLayer.createSAnnotation(null, TCFDictionary.ATT_SRC, "any source");
		EList<SNode> docWSNodes = docWSLayer.getSNodes();
		
		docTokens.get(1).createSAnnotation(TCFMapperImport.LAYER_WORDSENSE, TCFDictionary.ATT_LEXUNITS, "14");
		docTokens.get(1).createSAnnotation(TCFMapperImport.LAYER_WORDSENSE, TCFDictionary.ATT_COMMENT, "C.E.");
		docWSNodes.add(docTokens.get(1));
		
		SSpan newYork = docGraph.createSSpan(docTokens.get(2));
		docGraph.addSNode(newYork, docTokens.get(3), STYPE_NAME.SSPANNING_RELATION);
		newYork.createSAnnotation(TCFMapperImport.LAYER_WORDSENSE, TCFDictionary.ATT_LEXUNITS, "1 2 3");
		newYork.createSAnnotation(TCFMapperImport.LAYER_WORDSENSE, TCFDictionary.ATT_COMMENT, "from there to Germany");
		docWSNodes.add(newYork);		
		
		docTokens.get(4).createSAnnotation(TCFMapperImport.LAYER_WORDSENSE , TCFDictionary.ATT_LEXUNITS, "0");
		docWSNodes.add(docTokens.get(4));
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_LEXICALSEMANTICS);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		this.getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, true);
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, false);
		
		/* start mapper */
				
		this.getFixture().mapSDocument();
		
		
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = this.getFixture().getSDocument().getSDocumentGraph();
		assertEquals(1, fixGraph.getSLayerByName(TCFMapperImport.LAYER_WORDSENSE).size());
		SLayer fixWSLayer = fixGraph.getSLayerByName(TCFMapperImport.LAYER_WORDSENSE).get(0);
		assertEquals(docWSLayer.getSAnnotation(TCFDictionary.ATT_SRC), fixWSLayer.getSAnnotation(TCFDictionary.ATT_SRC));
		EList<SNode> fixWSNodes = fixWSLayer.getSNodes();
		assertEquals(docWSNodes.size(), fixWSNodes.size());		
		SNode docNode = null;
		SNode fixNode = null;
		for(int i=0; i<docWSNodes.size(); i++){
			docNode = docWSNodes.get(i);
			fixNode = fixWSNodes.get(i);
			assertEquals(docNode.getClass(), fixNode.getClass());
			assertEquals(docGraph.getSText(docNode), fixGraph.getSText(fixNode));
			assertEquals(docNode.getSAnnotation(TCFMapperImport.LAYER_WORDSENSE+"::"+TCFDictionary.ATT_LEXUNITS), fixNode.getSAnnotation(TCFMapperImport.LAYER_WORDSENSE+"::"+TCFDictionary.ATT_LEXUNITS));
			assertEquals(docNode.getSAnnotation(TCFMapperImport.LAYER_WORDSENSE+"::"+TCFDictionary.ATT_COMMENT), fixNode.getSAnnotation(TCFMapperImport.LAYER_WORDSENSE+"::"+TCFDictionary.ATT_COMMENT));
		}
	}
	
	/**This method tests if a valid TCF-XML-structure containing lexical-semantic
	 * annotations is converted to salt correctly 
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testWordSenseDisambiguationNotShrinked() throws XMLStreamException, FileNotFoundException{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o= XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);
		
		xmlWriter.writeStartDocument();
		xmlWriter.writeProcessingInstruction(TCFDictionary.TCF_PI);
			xmlWriter.writeStartElement(TCFDictionary.NS_WL, TCFDictionary.TAG_WL_D_SPIN, TCFDictionary.NS_VALUE_WL);
			xmlWriter.writeNamespace(TCFDictionary.NS_WL, TCFDictionary.NS_VALUE_WL);
			xmlWriter.writeNamespace(TCFDictionary.NS_ED, TCFDictionary.NS_VALUE_ED);
			xmlWriter.writeNamespace(TCFDictionary.NS_LX, TCFDictionary.NS_VALUE_LX);
			xmlWriter.writeNamespace(TCFDictionary.NS_MD, TCFDictionary.NS_VALUE_MD);
			xmlWriter.writeNamespace(TCFDictionary.NS_TC, TCFDictionary.NS_VALUE_TC);
			xmlWriter.writeAttribute(TCFDictionary.ATT_VERSION, "0.4");
				xmlWriter.writeStartElement(TCFDictionary.NS_MD, TCFDictionary.TAG_MD_METADATA, TCFDictionary.NS_VALUE_MD);
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTCORPUS, TCFDictionary.NS_VALUE_TC);
				xmlWriter.writeAttribute(TCFDictionary.ATT_LANG, "de");
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXT, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeCharacters(EXAMPLE_TEXT_SHRINK);
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKENS, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t1");
							xmlWriter.writeCharacters("I");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t2");
							xmlWriter.writeCharacters("love");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t3");
							xmlWriter.writeCharacters("New");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t4");
							xmlWriter.writeCharacters("York");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t5");
							xmlWriter.writeCharacters(".");
						xmlWriter.writeEndElement();						
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_WSD, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_SRC, "any source");
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_WS, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t2");
						xmlWriter.writeAttribute(TCFDictionary.ATT_LEXUNITS, "14");
						xmlWriter.writeAttribute(TCFDictionary.ATT_COMMENT, "C.E.");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_WS, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t3 t4");
						xmlWriter.writeAttribute(TCFDictionary.ATT_LEXUNITS, "1 2 3");
						xmlWriter.writeAttribute(TCFDictionary.ATT_COMMENT, "from there to Germany");
						xmlWriter.writeCharacters("");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_WS, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t5");
						xmlWriter.writeAttribute(TCFDictionary.ATT_LEXUNITS, "0");
						xmlWriter.writeCharacters("");
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();//end of wsd
				xmlWriter.writeEndElement();//End of TextCorpus
			xmlWriter.writeEndElement();
		xmlWriter.writeEndDocument();
		
		/* generating salt sample */
		SDocument doc = SaltFactory.eINSTANCE.createSDocument();
		SDocumentGraph docGraph = SaltFactory.eINSTANCE.createSDocumentGraph();
		doc.setSDocumentGraph(docGraph);
		docGraph.createSTextualDS(EXAMPLE_TEXT_SHRINK);
		docGraph.tokenize();
		EList<SToken> docTokens = docGraph.getSTokens();
		SLayer docWSLayer = SaltFactory.eINSTANCE.createSLayer();
		docWSLayer.setSName(TCFMapperImport.LAYER_WORDSENSE);
		docWSLayer.createSAnnotation(null, TCFDictionary.ATT_SRC, "any source");
		EList<SNode> docWSNodes = docWSLayer.getSNodes();
		
		SSpan sSpan = docGraph.createSSpan(docTokens.get(1));
		sSpan.createSAnnotation(TCFMapperImport.LAYER_WORDSENSE, TCFDictionary.ATT_LEXUNITS, "14");
		sSpan.createSAnnotation(TCFMapperImport.LAYER_WORDSENSE, TCFDictionary.ATT_COMMENT, "C.E.");
		docWSNodes.add(sSpan);
		
		sSpan = docGraph.createSSpan(docTokens.get(2));
		docGraph.addSNode(sSpan, docTokens.get(3), STYPE_NAME.SSPANNING_RELATION);
		sSpan.createSAnnotation(TCFMapperImport.LAYER_WORDSENSE, TCFDictionary.ATT_LEXUNITS, "1 2 3");
		sSpan.createSAnnotation(TCFMapperImport.LAYER_WORDSENSE, TCFDictionary.ATT_COMMENT, "from there to Germany");
		docWSNodes.add(sSpan);		
		
		sSpan = docGraph.createSSpan(docTokens.get(4));
		sSpan.createSAnnotation(TCFMapperImport.LAYER_WORDSENSE , TCFDictionary.ATT_LEXUNITS, "0");
		docWSNodes.add(sSpan);
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_LEXICALSEMANTICS);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		this.getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, false);
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, false);
		
		/* start mapper */
				
		this.getFixture().mapSDocument();
		
		
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = this.getFixture().getSDocument().getSDocumentGraph();
		assertEquals(1, fixGraph.getSLayerByName(TCFMapperImport.LAYER_WORDSENSE).size());
		SLayer fixWSLayer = fixGraph.getSLayerByName(TCFMapperImport.LAYER_WORDSENSE).get(0);
		assertEquals(docWSLayer.getSAnnotation(TCFDictionary.ATT_SRC), fixWSLayer.getSAnnotation(TCFDictionary.ATT_SRC));
		EList<SNode> fixWSNodes = fixWSLayer.getSNodes();
		assertEquals(docWSNodes.size(), fixWSNodes.size());		
		SNode docNode = null;
		SNode fixNode = null;
		for(int i=0; i<docWSNodes.size(); i++){
			docNode = docWSNodes.get(i);
			fixNode = fixWSNodes.get(i);
			assertTrue(fixNode instanceof SSpan);
			assertEquals(docGraph.getSText(docNode), fixGraph.getSText(fixNode));
			assertEquals(docNode.getSAnnotation(TCFMapperImport.LAYER_WORDSENSE+"::"+TCFDictionary.ATT_LEXUNITS), fixNode.getSAnnotation(TCFMapperImport.LAYER_WORDSENSE+"::"+TCFDictionary.ATT_LEXUNITS));
			assertEquals(docNode.getSAnnotation(TCFMapperImport.LAYER_WORDSENSE+"::"+TCFDictionary.ATT_COMMENT), fixNode.getSAnnotation(TCFMapperImport.LAYER_WORDSENSE+"::"+TCFDictionary.ATT_COMMENT));
		}
	}
		
	/**This method tests if a valid TCF-XML-structure containing word-splitting
	 * annotations is converted to salt correctly 
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testWordSplittingsShrinked() throws FileNotFoundException, XMLStreamException{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o= XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);
		
		xmlWriter.writeStartDocument();
		xmlWriter.writeProcessingInstruction(TCFDictionary.TCF_PI);
			xmlWriter.writeStartElement(TCFDictionary.NS_WL, TCFDictionary.TAG_WL_D_SPIN, TCFDictionary.NS_VALUE_WL);
			xmlWriter.writeNamespace(TCFDictionary.NS_WL, TCFDictionary.NS_VALUE_WL);
			xmlWriter.writeNamespace(TCFDictionary.NS_ED, TCFDictionary.NS_VALUE_ED);
			xmlWriter.writeNamespace(TCFDictionary.NS_LX, TCFDictionary.NS_VALUE_LX);
			xmlWriter.writeNamespace(TCFDictionary.NS_MD, TCFDictionary.NS_VALUE_MD);
			xmlWriter.writeNamespace(TCFDictionary.NS_TC, TCFDictionary.NS_VALUE_TC);
			xmlWriter.writeAttribute(TCFDictionary.ATT_VERSION, "0.4");
				xmlWriter.writeStartElement(TCFDictionary.NS_MD, TCFDictionary.TAG_MD_METADATA, TCFDictionary.NS_VALUE_MD);
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTCORPUS, TCFDictionary.NS_VALUE_TC);
				xmlWriter.writeAttribute(TCFDictionary.ATT_LANG, "de");
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXT, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeCharacters(EXAMPLE_TEXT);
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKENS, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t1");
							xmlWriter.writeCharacters("Is");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t2");
							xmlWriter.writeCharacters("this");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t3");
							xmlWriter.writeCharacters("example");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t4");
							xmlWriter.writeCharacters("more");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t5");
							xmlWriter.writeCharacters("complicated");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t6");
							xmlWriter.writeCharacters("than");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t7");
							xmlWriter.writeCharacters("it");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t8");
							xmlWriter.writeCharacters("appears");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t9");
							xmlWriter.writeCharacters("to");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t10");
							xmlWriter.writeCharacters("be");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t10");
							xmlWriter.writeCharacters("?");
						xmlWriter.writeEndElement();	
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_WORDSPLITTINGS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "syllables");
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SPLIT, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKID, "t2");
						xmlWriter.writeCharacters("1");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SPLIT, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKID, "t5");
						xmlWriter.writeCharacters("3 6 8");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SPLIT, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKID, "t8");
						xmlWriter.writeCharacters("2");
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();//end of WordSplittings
				xmlWriter.writeEndElement();//End of TextCorpus
			xmlWriter.writeEndElement();
		xmlWriter.writeEndDocument();
		
		/* generating salt sample */
		SDocument doc = SaltFactory.eINSTANCE.createSDocument();
		doc.setSDocumentGraph(SaltFactory.eINSTANCE.createSDocumentGraph());
		SaltSample.createPrimaryData(doc);
		SaltSample.createTokens2(doc);
		SDocumentGraph docGraph = doc.getSDocumentGraph();
		EList<SToken> docTokens = docGraph.getSTokens();
		SLayer docSplitLayer = SaltFactory.eINSTANCE.createSLayer();
		docSplitLayer.setSName(TCFMapperImport.LAYER_SPLITTINGS);		
		EList<SNode> docSplitNodes = docSplitLayer.getSNodes();
		
		docSplitLayer.createSAnnotation(null, TCFDictionary.ATT_TYPE, "syllables");
		//example:
		docTokens.get(1).createSAnnotation(TCFMapperImport.LAYER_SPLITTINGS, TCFDictionary.TAG_TC_WORDSPLITTINGS, "1"); //I know ...
		docSplitNodes.add(docTokens.get(1));
		
		//complicated:
		docTokens.get(4).createSAnnotation(TCFMapperImport.LAYER_SPLITTINGS, TCFDictionary.TAG_TC_WORDSPLITTINGS, "3 6 8");
		docSplitNodes.add(docTokens.get(4));
		
		//appears:
		docTokens.get(7).createSAnnotation(TCFMapperImport.LAYER_SPLITTINGS, TCFDictionary.TAG_TC_WORDSPLITTINGS, "2");
		docSplitNodes.add(docTokens.get(7));
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_LEXICALSEMANTICS);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		this.getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, true);
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, false);
		
		/* start mapper */
				
		this.getFixture().mapSDocument();
		
		logger.debug("...");
		
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = this.getFixture().getSDocument().getSDocumentGraph();
		assertEquals(1, fixGraph.getSLayerByName(TCFMapperImport.LAYER_SPLITTINGS).size());
		SLayer fixSplitLayer = fixGraph.getSLayerByName(TCFMapperImport.LAYER_SPLITTINGS).get(0);
		assertEquals(docSplitLayer.getSAnnotation(TCFDictionary.ATT_TYPE), fixSplitLayer.getSAnnotation(TCFDictionary.ATT_TYPE));
		EList<SNode> fixSplitNodes = fixSplitLayer.getSNodes();
		assertEquals(docSplitNodes.size(), fixSplitNodes.size());
		SNode docNode = null;
		SNode fixNode = null;
		for(int i=0; i<docSplitNodes.size(); i++){
			docNode = docSplitNodes.get(i);
			fixNode = fixSplitNodes.get(i);
			assertEquals(docNode.getClass(), fixNode.getClass());
			assertEquals(docGraph.getSText(docNode), fixGraph.getSText(fixNode));
			assertEquals(docNode.getSAnnotation(TCFMapperImport.LAYER_SPLITTINGS+"::"+TCFDictionary.TAG_TC_WORDSPLITTINGS), docNode.getSAnnotation(TCFMapperImport.LAYER_SPLITTINGS+"::"+TCFDictionary.TAG_TC_WORDSPLITTINGS));
		}
	}
	
	/**This method tests if a valid TCF-XML-structure containing word-splitting
	 * annotations is converted to salt correctly 
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testWordSplittingsNotShrinked() throws FileNotFoundException, XMLStreamException{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o= XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);
		
		xmlWriter.writeStartDocument();
		xmlWriter.writeProcessingInstruction(TCFDictionary.TCF_PI);
			xmlWriter.writeStartElement(TCFDictionary.NS_WL, TCFDictionary.TAG_WL_D_SPIN, TCFDictionary.NS_VALUE_WL);
			xmlWriter.writeNamespace(TCFDictionary.NS_WL, TCFDictionary.NS_VALUE_WL);
			xmlWriter.writeNamespace(TCFDictionary.NS_ED, TCFDictionary.NS_VALUE_ED);
			xmlWriter.writeNamespace(TCFDictionary.NS_LX, TCFDictionary.NS_VALUE_LX);
			xmlWriter.writeNamespace(TCFDictionary.NS_MD, TCFDictionary.NS_VALUE_MD);
			xmlWriter.writeNamespace(TCFDictionary.NS_TC, TCFDictionary.NS_VALUE_TC);
			xmlWriter.writeAttribute(TCFDictionary.ATT_VERSION, "0.4");
				xmlWriter.writeStartElement(TCFDictionary.NS_MD, TCFDictionary.TAG_MD_METADATA, TCFDictionary.NS_VALUE_MD);
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTCORPUS, TCFDictionary.NS_VALUE_TC);
				xmlWriter.writeAttribute(TCFDictionary.ATT_LANG, "de");
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXT, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeCharacters(EXAMPLE_TEXT);
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKENS, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t1");
							xmlWriter.writeCharacters("Is");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t2");
							xmlWriter.writeCharacters("this");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t3");
							xmlWriter.writeCharacters("example");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t4");
							xmlWriter.writeCharacters("more");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t5");
							xmlWriter.writeCharacters("complicated");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t6");
							xmlWriter.writeCharacters("than");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t7");
							xmlWriter.writeCharacters("it");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t8");
							xmlWriter.writeCharacters("appears");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t9");
							xmlWriter.writeCharacters("to");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t10");
							xmlWriter.writeCharacters("be");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t10");
							xmlWriter.writeCharacters("?");
						xmlWriter.writeEndElement();	
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_WORDSPLITTINGS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "syllables");
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SPLIT, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKID, "t2");
						xmlWriter.writeCharacters("1");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SPLIT, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKID, "t5");
						xmlWriter.writeCharacters("3 6 8");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_SPLIT, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKID, "t8");
						xmlWriter.writeCharacters("2");
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();//end of WordSplittings
				xmlWriter.writeEndElement();//End of TextCorpus
			xmlWriter.writeEndElement();
		xmlWriter.writeEndDocument();
		
		/* generating salt sample */
		SDocument doc = SaltFactory.eINSTANCE.createSDocument();
		doc.setSDocumentGraph(SaltFactory.eINSTANCE.createSDocumentGraph());
		SaltSample.createPrimaryData(doc);
		SaltSample.createTokens2(doc);
		SDocumentGraph docGraph = doc.getSDocumentGraph();
		EList<SToken> docTokens = docGraph.getSTokens();
		SLayer docSplitLayer = SaltFactory.eINSTANCE.createSLayer();
		docSplitLayer.setSName(TCFMapperImport.LAYER_SPLITTINGS);		
		EList<SNode> docSplitNodes = docSplitLayer.getSNodes();
		
		docSplitLayer.createSAnnotation(null, TCFDictionary.ATT_TYPE, "syllables");
		//example:
		SSpan sSpan = docGraph.createSSpan(docTokens.get(1));
		sSpan.createSAnnotation(TCFMapperImport.LAYER_SPLITTINGS, TCFDictionary.TAG_TC_WORDSPLITTINGS, "1"); //I know ...
		docSplitNodes.add(sSpan);
		
		//complicated:
		sSpan = docGraph.createSSpan(docTokens.get(4));
		sSpan.createSAnnotation(TCFMapperImport.LAYER_SPLITTINGS, TCFDictionary.TAG_TC_WORDSPLITTINGS, "3 6 8"); //I know ...
		docSplitNodes.add(sSpan);
		
		//appears:
		sSpan = docGraph.createSSpan(docTokens.get(7));
		sSpan.createSAnnotation(TCFMapperImport.LAYER_SPLITTINGS, TCFDictionary.TAG_TC_WORDSPLITTINGS, "2"); //I know ...
		docSplitNodes.add(sSpan);
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_LEXICALSEMANTICS);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		this.getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, false);
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, false);
		
		/* start mapper */
				
		this.getFixture().mapSDocument();
		
		
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = this.getFixture().getSDocument().getSDocumentGraph();
		assertEquals(1, fixGraph.getSLayerByName(TCFMapperImport.LAYER_SPLITTINGS).size());
		SLayer fixSplitLayer = fixGraph.getSLayerByName(TCFMapperImport.LAYER_SPLITTINGS).get(0);
		assertEquals(docSplitLayer.getSAnnotation(TCFDictionary.ATT_TYPE), fixSplitLayer.getSAnnotation(TCFDictionary.ATT_TYPE));
		EList<SNode> fixSplitNodes = fixSplitLayer.getSNodes();
		assertEquals(docSplitNodes.size(), fixSplitNodes.size());
		SNode docNode = null;
		SNode fixNode = null;
		for(int i=0; i<docSplitNodes.size(); i++){
			docNode = docSplitNodes.get(i);
			fixNode = fixSplitNodes.get(i);
			assertTrue(fixNode instanceof SSpan);
			assertEquals(docGraph.getSText(docNode), fixGraph.getSText(fixNode));
			assertEquals(docNode.getSAnnotation(TCFMapperImport.LAYER_SPLITTINGS+"::"+TCFDictionary.TAG_TC_WORDSPLITTINGS), docNode.getSAnnotation(TCFMapperImport.LAYER_SPLITTINGS+"::"+TCFDictionary.TAG_TC_WORDSPLITTINGS));
		}
	}
		
	/**This method tests if a valid TCF-XML-structure containing discourse connectives
	 * annotations is converted to salt correctly 
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testDiscourseConnectivesShrinked() throws XMLStreamException, FileNotFoundException{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o= XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);		
		
		xmlWriter.writeStartDocument();
			xmlWriter.writeProcessingInstruction(TCFDictionary.TCF_PI);
			xmlWriter.writeStartElement(TCFDictionary.NS_WL, TCFDictionary.TAG_WL_D_SPIN, TCFDictionary.NS_VALUE_WL);
			xmlWriter.writeNamespace(TCFDictionary.NS_WL, TCFDictionary.NS_VALUE_WL);
			xmlWriter.writeNamespace(TCFDictionary.NS_ED, TCFDictionary.NS_VALUE_ED);
			xmlWriter.writeNamespace(TCFDictionary.NS_LX, TCFDictionary.NS_VALUE_LX);
			xmlWriter.writeNamespace(TCFDictionary.NS_MD, TCFDictionary.NS_VALUE_MD);
			xmlWriter.writeNamespace(TCFDictionary.NS_TC, TCFDictionary.NS_VALUE_TC);
			xmlWriter.writeAttribute(TCFDictionary.ATT_VERSION, "0.4");
			xmlWriter.writeAttribute(TCFDictionary.ATT_LANG, "en");
				xmlWriter.writeStartElement(TCFDictionary.NS_MD, TCFDictionary.TAG_MD_METADATA, TCFDictionary.NS_VALUE_MD);
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTCORPUS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXT, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeCharacters(EXAMPLE_TEXT_DISCOURSE_CONNECTIVES);
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKENS, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t1");
							xmlWriter.writeCharacters("Since");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t2");
							xmlWriter.writeCharacters("I");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t3");
							xmlWriter.writeCharacters("went");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t4");
							xmlWriter.writeCharacters("there");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t5");
							xmlWriter.writeCharacters("I");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t6");
							xmlWriter.writeCharacters("know");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t7");
							xmlWriter.writeCharacters("more");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t8");
							xmlWriter.writeCharacters("than");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t9");
							xmlWriter.writeCharacters("I");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t10");
							xmlWriter.writeCharacters("knew");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t11");
							xmlWriter.writeCharacters("before");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t12");
							xmlWriter.writeCharacters(".");
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();//end of tokens
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_DISCOURSECONNECTIVES, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TAGSET, "any tagset");
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONNECTIVE, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t1");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "temporal");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONNECTIVE, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t8");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "comparative");
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();
				xmlWriter.writeEndElement();
			xmlWriter.writeEndElement();
		xmlWriter.writeEndDocument();
		
		/* generating salt sample */
		SDocument doc = SaltFactory.eINSTANCE.createSDocument();
		SDocumentGraph docGraph = SaltFactory.eINSTANCE.createSDocumentGraph();
		doc.setSDocumentGraph(docGraph);
		docGraph.createSTextualDS(EXAMPLE_TEXT_DISCOURSE_CONNECTIVES);
		docGraph.tokenize();
		EList<SToken> docTokens = docGraph.getSTokens();
		SLayer docDiscourseLayer = SaltFactory.eINSTANCE.createSLayer();
		docDiscourseLayer.setSName(TCFMapperImport.LAYER_DISCOURSE);
		docGraph.addSLayer(docDiscourseLayer);
		EList<SNode> docDiscourseNodes = docDiscourseLayer.getSNodes();
		
		docDiscourseLayer.createSMetaAnnotation(null, TCFDictionary.ATT_TAGSET, "any tagset");
		
		//since:
		docTokens.get(0).createSAnnotation(TCFMapperImport.LAYER_DISCOURSE, TCFDictionary.ATT_TYPE, "temporal");
		docDiscourseNodes.add(docTokens.get(0));
		//than:
		docTokens.get(7).createSAnnotation(TCFMapperImport.LAYER_DISCOURSE, TCFDictionary.ATT_TYPE, "comparative");
		docDiscourseNodes.add(docTokens.get(7));
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_DISCOURSE_CONNECTIVES);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		this.getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, true);
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, false);
		
		/* start mapper */
				
		this.getFixture().mapSDocument();
		
		
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = this.getFixture().getSDocument().getSDocumentGraph();
		assertEquals(1, fixGraph.getSLayerByName(TCFMapperImport.LAYER_DISCOURSE).size());
		SLayer fixDiscourseLayer = fixGraph.getSLayerByName(TCFMapperImport.LAYER_DISCOURSE).get(0);
		assertEquals(docDiscourseLayer.getSMetaAnnotation(TCFDictionary.ATT_TAGSET), fixDiscourseLayer.getSMetaAnnotation(TCFDictionary.ATT_TAGSET));
		EList<SNode> fixDiscourseNodes = fixDiscourseLayer.getSNodes();
		assertEquals(docDiscourseNodes.size(), fixDiscourseNodes.size());
		SNode docNode = null;
		SNode fixNode = null;
		for(int i=0; i<docDiscourseNodes.size(); i++){
			docNode = docDiscourseNodes.get(i);
			fixNode = fixDiscourseNodes.get(i);
			assertEquals(docNode.getClass(), fixNode.getClass());
			assertEquals(docGraph.getSText(docNode), fixGraph.getSText(fixNode));
			assertEquals(docNode.getSAnnotation(TCFMapperImport.LAYER_DISCOURSE+"::"+TCFDictionary.ATT_TYPE), fixNode.getSAnnotation(TCFMapperImport.LAYER_DISCOURSE+"::"+TCFDictionary.ATT_TYPE));
		}
	}
	
	/**This method tests if a valid TCF-XML-structure containing discourse connectives
	 * annotations is converted to salt correctly 
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testDiscourseConnectivesNotShrinked() throws FileNotFoundException, XMLStreamException{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o= XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);		
		
		xmlWriter.writeStartDocument();
			xmlWriter.writeProcessingInstruction(TCFDictionary.TCF_PI);
			xmlWriter.writeStartElement(TCFDictionary.NS_WL, TCFDictionary.TAG_WL_D_SPIN, TCFDictionary.NS_VALUE_WL);
			xmlWriter.writeNamespace(TCFDictionary.NS_WL, TCFDictionary.NS_VALUE_WL);
			xmlWriter.writeNamespace(TCFDictionary.NS_ED, TCFDictionary.NS_VALUE_ED);
			xmlWriter.writeNamespace(TCFDictionary.NS_LX, TCFDictionary.NS_VALUE_LX);
			xmlWriter.writeNamespace(TCFDictionary.NS_MD, TCFDictionary.NS_VALUE_MD);
			xmlWriter.writeNamespace(TCFDictionary.NS_TC, TCFDictionary.NS_VALUE_TC);
			xmlWriter.writeAttribute(TCFDictionary.ATT_VERSION, "0.4");
			xmlWriter.writeAttribute(TCFDictionary.ATT_LANG, "en");
				xmlWriter.writeStartElement(TCFDictionary.NS_MD, TCFDictionary.TAG_MD_METADATA, TCFDictionary.NS_VALUE_MD);
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTCORPUS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXT, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeCharacters(EXAMPLE_TEXT_DISCOURSE_CONNECTIVES);
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKENS, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t1");
							xmlWriter.writeCharacters("Since");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t2");
							xmlWriter.writeCharacters("I");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t3");
							xmlWriter.writeCharacters("went");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t4");
							xmlWriter.writeCharacters("there");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t5");
							xmlWriter.writeCharacters("I");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t6");
							xmlWriter.writeCharacters("know");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t7");
							xmlWriter.writeCharacters("more");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t8");
							xmlWriter.writeCharacters("than");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t9");
							xmlWriter.writeCharacters("I");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t10");
							xmlWriter.writeCharacters("knew");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t11");
							xmlWriter.writeCharacters("before");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
							xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t12");
							xmlWriter.writeCharacters(".");
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();//end of tokens
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_DISCOURSECONNECTIVES, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TAGSET, "any tagset");
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONNECTIVE, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t1");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "temporal");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_CONNECTIVE, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t8");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "comparative");
						xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();
				xmlWriter.writeEndElement();
			xmlWriter.writeEndElement();
		xmlWriter.writeEndDocument();
		
		/* generating salt sample */
		SDocument doc = SaltFactory.eINSTANCE.createSDocument();
		SDocumentGraph docGraph = SaltFactory.eINSTANCE.createSDocumentGraph();
		doc.setSDocumentGraph(docGraph);
		docGraph.createSTextualDS(EXAMPLE_TEXT_DISCOURSE_CONNECTIVES);
		docGraph.tokenize();
		EList<SToken> docTokens = docGraph.getSTokens();
		SLayer docDiscourseLayer = SaltFactory.eINSTANCE.createSLayer();
		docDiscourseLayer.setSName(TCFMapperImport.LAYER_DISCOURSE);
		docGraph.addSLayer(docDiscourseLayer);
		EList<SNode> docDiscourseNodes = docDiscourseLayer.getSNodes();
		
		docDiscourseLayer.createSMetaAnnotation(null, TCFDictionary.ATT_TAGSET, "any tagset");
		
		//since:
		SSpan sSpan = docGraph.createSSpan(docTokens.get(0));
		sSpan.createSAnnotation(TCFMapperImport.LAYER_DISCOURSE, TCFDictionary.ATT_TYPE, "temporal");
		docDiscourseNodes.add(sSpan);
		//than:
		sSpan = docGraph.createSSpan(docTokens.get(7));
		sSpan.createSAnnotation(TCFMapperImport.LAYER_DISCOURSE, TCFDictionary.ATT_TYPE, "comparative");
		docDiscourseNodes.add(sSpan);
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_DISCOURSE_CONNECTIVES);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		this.getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, false);
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, false);
		
		/* start mapper */
				
		this.getFixture().mapSDocument();
		
		
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = this.getFixture().getSDocument().getSDocumentGraph();
		assertEquals(1, fixGraph.getSLayerByName(TCFMapperImport.LAYER_DISCOURSE).size());
		SLayer fixDiscourseLayer = fixGraph.getSLayerByName(TCFMapperImport.LAYER_DISCOURSE).get(0);
		assertEquals(docDiscourseLayer.getSMetaAnnotation(TCFDictionary.ATT_TAGSET), fixDiscourseLayer.getSMetaAnnotation(TCFDictionary.ATT_TAGSET));
		EList<SNode> fixDiscourseNodes = fixDiscourseLayer.getSNodes();
		assertEquals(docDiscourseNodes.size(), fixDiscourseNodes.size());
		SNode docNode = null;
		SNode fixNode = null;
		for(int i=0; i<docDiscourseNodes.size(); i++){
			docNode = docDiscourseNodes.get(i);
			fixNode = fixDiscourseNodes.get(i);
			assertTrue(fixNode instanceof SSpan);
			assertEquals(docGraph.getSText(docNode), fixGraph.getSText(fixNode));
			assertEquals(docNode.getSAnnotation(TCFMapperImport.LAYER_DISCOURSE+"::"+TCFDictionary.ATT_TYPE), fixNode.getSAnnotation(TCFMapperImport.LAYER_DISCOURSE+"::"+TCFDictionary.ATT_TYPE));
		}
	}
	
	/**This method tests if a valid TCF-XML-structure containing text structure
	 * annotations is converted to salt correctly 
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException */
	@Test
	public void testTextStructureShrinked() throws XMLStreamException, FileNotFoundException{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o= XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);
		
		xmlWriter.writeStartDocument();
		xmlWriter.writeProcessingInstruction(TCFDictionary.TCF_PI);
		xmlWriter.writeStartElement(TCFDictionary.NS_WL, TCFDictionary.TAG_WL_D_SPIN, TCFDictionary.NS_VALUE_WL);
		xmlWriter.writeNamespace(TCFDictionary.NS_WL, TCFDictionary.NS_VALUE_WL);
		xmlWriter.writeNamespace(TCFDictionary.NS_ED, TCFDictionary.NS_VALUE_ED);
		xmlWriter.writeNamespace(TCFDictionary.NS_LX, TCFDictionary.NS_VALUE_LX);
		xmlWriter.writeNamespace(TCFDictionary.NS_MD, TCFDictionary.NS_VALUE_MD);
		xmlWriter.writeNamespace(TCFDictionary.NS_TC, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_VERSION, "4.0");
			xmlWriter.writeStartElement(TCFDictionary.NS_MD, TCFDictionary.TAG_MD_METADATA, TCFDictionary.NS_VALUE_MD);
			xmlWriter.writeEndElement();
			xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTCORPUS, TCFDictionary.NS_VALUE_TC);
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXT, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeCharacters(EXAMPLE_TEXT);
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKENS, TCFDictionary.NS_VALUE_TC);					
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKENS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t1");
						xmlWriter.writeCharacters("Is");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t2");
						xmlWriter.writeCharacters("this");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t3");
						xmlWriter.writeCharacters("example");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t4");
						xmlWriter.writeCharacters("more");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t5");
						xmlWriter.writeCharacters("complicated");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t6");
						xmlWriter.writeCharacters("than");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t7");
						xmlWriter.writeCharacters("it");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t8");
						xmlWriter.writeCharacters("appears");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t9");
						xmlWriter.writeCharacters("to");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t10");
						xmlWriter.writeCharacters("be");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t11");
						xmlWriter.writeCharacters("?");
					xmlWriter.writeEndElement();					
					xmlWriter.writeEndElement();					
				xmlWriter.writeEndElement();//end of tokens
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTSTRUCTURE, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTSPAN, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_START, "t1");
					xmlWriter.writeAttribute(TCFDictionary.ATT_END, "t11");
					xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "paragraph");					
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTSPAN, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_START, "t1");
					xmlWriter.writeAttribute(TCFDictionary.ATT_END, "t5");
					xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "page");					
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTSPAN, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_START, "t6");
					xmlWriter.writeAttribute(TCFDictionary.ATT_END, "t11");
					xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "page");					
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTSPAN, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_START, "t1");
					xmlWriter.writeAttribute(TCFDictionary.ATT_END, "t1");
					xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "line");					
					xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTSPAN, TCFDictionary.NS_VALUE_TC);					
					xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "line");					
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTSPAN, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_START, "t2");
					xmlWriter.writeAttribute(TCFDictionary.ATT_END, "t5");
					xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "line");					
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTSPAN, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_START, "t6");
					xmlWriter.writeAttribute(TCFDictionary.ATT_END, "t11");
					xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "line");					
					xmlWriter.writeEndElement();
				xmlWriter.writeEndElement();
			xmlWriter.writeEndElement();
		xmlWriter.writeEndDocument();
		
		/* generating salt sample */
		SDocument doc = SaltFactory.eINSTANCE.createSDocument();
		SDocumentGraph docGraph = SaltFactory.eINSTANCE.createSDocumentGraph();
		doc.setSDocumentGraph(docGraph);
		docGraph.createSTextualDS(EXAMPLE_TEXT);
		docGraph.tokenize();
		EList<SToken> docTokens = docGraph.getSTokens();
		SLayer docTextLayer = SaltFactory.eINSTANCE.createSLayer();
		docTextLayer.setSName(TCFMapperImport.LAYER_TEXTSTRUCTURE);
		EList<SNode> docTextNodes = docTextLayer.getSNodes();
		
		//paragraph:
		SSpan sSpan = docGraph.createSSpan(docTokens);
		sSpan.createSAnnotation(TCFMapperImport.LAYER_TEXTSTRUCTURE, TCFDictionary.ATT_TYPE, "paragraph");
		docTextNodes.add(sSpan);
		
		//page1:
		EList<SToken> spanTokens = new BasicEList<SToken>();
		spanTokens.add(docTokens.get(0));
		spanTokens.add(docTokens.get(1));
		spanTokens.add(docTokens.get(2));
		spanTokens.add(docTokens.get(3));
		spanTokens.add(docTokens.get(4));
		sSpan = docGraph.createSSpan(spanTokens);
		sSpan.createSAnnotation(TCFMapperImport.LAYER_TEXTSTRUCTURE, TCFDictionary.ATT_TYPE, "page");
		docTextNodes.add(sSpan);
		
		//page2:
		spanTokens.clear();
		spanTokens.add(docTokens.get(5));
		spanTokens.add(docTokens.get(6));
		spanTokens.add(docTokens.get(7));
		spanTokens.add(docTokens.get(8));
		spanTokens.add(docTokens.get(9));
		spanTokens.add(docTokens.get(10));
		sSpan = docGraph.createSSpan(spanTokens);
		sSpan.createSAnnotation(TCFMapperImport.LAYER_TEXTSTRUCTURE, TCFDictionary.ATT_TYPE, "page");
		docTextNodes.add(sSpan);
		
		//line1:
		docTokens.get(0).createSAnnotation(TCFMapperImport.LAYER_TEXTSTRUCTURE, TCFDictionary.ATT_TYPE, "line");
		docTextNodes.add(docTokens.get(0));
		
		//line2:
		spanTokens.clear();
		spanTokens.add(docTokens.get(1));
		spanTokens.add(docTokens.get(2));
		spanTokens.add(docTokens.get(3));
		spanTokens.add(docTokens.get(4));
		sSpan = docGraph.createSSpan(spanTokens);
		sSpan.createSAnnotation(TCFMapperImport.LAYER_TEXTSTRUCTURE, TCFDictionary.ATT_TYPE, "line");
		docTextNodes.add(sSpan);
		
		//line3:
		spanTokens.clear();
		spanTokens.add(docTokens.get(5));
		spanTokens.add(docTokens.get(6));
		spanTokens.add(docTokens.get(7));
		spanTokens.add(docTokens.get(8));
		spanTokens.add(docTokens.get(9));
		spanTokens.add(docTokens.get(10));
		sSpan = docGraph.createSSpan(spanTokens);
		sSpan.createSAnnotation(TCFMapperImport.LAYER_TEXTSTRUCTURE, TCFDictionary.ATT_TYPE, "line");
		docTextNodes.add(sSpan);
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_TEXTSTRUCTURE);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		this.getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, true);
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, false);
		
		/* start mapper */
				
		this.getFixture().mapSDocument();
		
				
		
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = this.getFixture().getSDocument().getSDocumentGraph();
		assertEquals(1, fixGraph.getSLayerByName(TCFMapperImport.LAYER_TEXTSTRUCTURE).size());
		EList<SNode> fixTextNodes = fixGraph.getSLayerByName(TCFMapperImport.LAYER_TEXTSTRUCTURE).get(0).getSNodes();
		assertEquals(docTextNodes.size(), fixTextNodes.size());
		SNode docNode = null;
		SNode fixNode = null;
		for(int i=0; i<docTextNodes.size(); i++){
			docNode = docTextNodes.get(i);
			fixNode = fixTextNodes.get(i);
			assertEquals(docNode.getClass(), fixNode.getClass());
			assertEquals(docNode.getSAnnotation(TCFMapperImport.LAYER_TEXTSTRUCTURE+"::"+TCFDictionary.ATT_TYPE), docNode.getSAnnotation(TCFMapperImport.LAYER_TEXTSTRUCTURE+"::"+TCFDictionary.ATT_TYPE));
			assertEquals(docGraph.getSText(docNode), fixGraph.getSText(fixNode));
		}
	}

	/**This method tests if a valid TCF-XML-structure containing text structure
	 * annotations is converted to salt correctly 
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException */
	@Test
	public void testTextStructureNotShrinked() throws XMLStreamException, FileNotFoundException{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLOutputFactory o= XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);
		
		xmlWriter.writeStartDocument();
		xmlWriter.writeProcessingInstruction(TCFDictionary.TCF_PI);
		xmlWriter.writeStartElement(TCFDictionary.NS_WL, TCFDictionary.TAG_WL_D_SPIN, TCFDictionary.NS_VALUE_WL);
		xmlWriter.writeNamespace(TCFDictionary.NS_WL, TCFDictionary.NS_VALUE_WL);
		xmlWriter.writeNamespace(TCFDictionary.NS_ED, TCFDictionary.NS_VALUE_ED);
		xmlWriter.writeNamespace(TCFDictionary.NS_LX, TCFDictionary.NS_VALUE_LX);
		xmlWriter.writeNamespace(TCFDictionary.NS_MD, TCFDictionary.NS_VALUE_MD);
		xmlWriter.writeNamespace(TCFDictionary.NS_TC, TCFDictionary.NS_VALUE_TC);
		xmlWriter.writeAttribute(TCFDictionary.ATT_VERSION, "0.4");
			xmlWriter.writeStartElement(TCFDictionary.NS_MD, TCFDictionary.TAG_MD_METADATA, TCFDictionary.NS_VALUE_MD);
			xmlWriter.writeEndElement();
			xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTCORPUS, TCFDictionary.NS_VALUE_TC);
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXT, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeCharacters(EXAMPLE_TEXT);
				xmlWriter.writeEndElement();
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKENS, TCFDictionary.NS_VALUE_TC);					
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKENS, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t1");
						xmlWriter.writeCharacters("Is");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t2");
						xmlWriter.writeCharacters("this");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t3");
						xmlWriter.writeCharacters("example");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t4");
						xmlWriter.writeCharacters("more");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t5");
						xmlWriter.writeCharacters("complicated");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t6");
						xmlWriter.writeCharacters("than");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t7");
						xmlWriter.writeCharacters("it");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t8");
						xmlWriter.writeCharacters("appears");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t9");
						xmlWriter.writeCharacters("to");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t10");
						xmlWriter.writeCharacters("be");
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t11");
						xmlWriter.writeCharacters("?");
					xmlWriter.writeEndElement();					
					xmlWriter.writeEndElement();					
				xmlWriter.writeEndElement();//end of tokens
				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTSTRUCTURE, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTSPAN, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_START, "t1");
					xmlWriter.writeAttribute(TCFDictionary.ATT_END, "t11");
					xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "paragraph");					
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTSPAN, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_START, "t1");
					xmlWriter.writeAttribute(TCFDictionary.ATT_END, "t5");
					xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "page");					
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTSPAN, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_START, "t6");
					xmlWriter.writeAttribute(TCFDictionary.ATT_END, "t11");
					xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "page");					
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTSPAN, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_START, "t1");
					xmlWriter.writeAttribute(TCFDictionary.ATT_END, "t1");
					xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "line");					
					xmlWriter.writeEndElement();
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTSPAN, TCFDictionary.NS_VALUE_TC);					
					xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "line");					
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTSPAN, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_START, "t2");
					xmlWriter.writeAttribute(TCFDictionary.ATT_END, "t5");
					xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "line");					
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TEXTSPAN, TCFDictionary.NS_VALUE_TC);
					xmlWriter.writeAttribute(TCFDictionary.ATT_START, "t6");
					xmlWriter.writeAttribute(TCFDictionary.ATT_END, "t11");
					xmlWriter.writeAttribute(TCFDictionary.ATT_TYPE, "line");					
					xmlWriter.writeEndElement();
				xmlWriter.writeEndElement();
			xmlWriter.writeEndElement();
		xmlWriter.writeEndDocument();
		
		/* generating salt sample */
		SDocument doc = SaltFactory.eINSTANCE.createSDocument();
		SDocumentGraph docGraph = SaltFactory.eINSTANCE.createSDocumentGraph();
		doc.setSDocumentGraph(docGraph);
		docGraph.createSTextualDS(EXAMPLE_TEXT);
		docGraph.tokenize();
		EList<SToken> docTokens = docGraph.getSTokens();
		SLayer docTextLayer = SaltFactory.eINSTANCE.createSLayer();
		docTextLayer.setSName(TCFMapperImport.LAYER_TEXTSTRUCTURE);
		EList<SNode> docTextNodes = docTextLayer.getSNodes();
		
		//paragraph:
		SSpan sSpan = docGraph.createSSpan(docTokens);
		sSpan.createSAnnotation(TCFMapperImport.LAYER_TEXTSTRUCTURE, TCFDictionary.ATT_TYPE, "paragraph");
		docTextNodes.add(sSpan);
		
		//page1:
		EList<SToken> spanTokens = new BasicEList<SToken>();
		spanTokens.add(docTokens.get(0));
		spanTokens.add(docTokens.get(1));
		spanTokens.add(docTokens.get(2));
		spanTokens.add(docTokens.get(3));
		spanTokens.add(docTokens.get(4));
		sSpan = docGraph.createSSpan(spanTokens);
		sSpan.createSAnnotation(TCFMapperImport.LAYER_TEXTSTRUCTURE, TCFDictionary.ATT_TYPE, "page");
		docTextNodes.add(sSpan);
		
		//page2:
		spanTokens.clear();
		spanTokens.add(docTokens.get(5));
		spanTokens.add(docTokens.get(6));
		spanTokens.add(docTokens.get(7));
		spanTokens.add(docTokens.get(8));
		spanTokens.add(docTokens.get(9));
		spanTokens.add(docTokens.get(10));
		sSpan = docGraph.createSSpan(spanTokens);
		sSpan.createSAnnotation(TCFMapperImport.LAYER_TEXTSTRUCTURE, TCFDictionary.ATT_TYPE, "page");
		docTextNodes.add(sSpan);
		
		//line1:
		sSpan = docGraph.createSSpan(docTokens.get(0));
		sSpan.createSAnnotation(TCFMapperImport.LAYER_TEXTSTRUCTURE, TCFDictionary.ATT_TYPE, "line");
		docTextNodes.add(sSpan);
		
		//line2:
		spanTokens.clear();
		spanTokens.add(docTokens.get(1));
		spanTokens.add(docTokens.get(2));
		spanTokens.add(docTokens.get(3));
		spanTokens.add(docTokens.get(4));
		sSpan = docGraph.createSSpan(spanTokens);
		sSpan.createSAnnotation(TCFMapperImport.LAYER_TEXTSTRUCTURE, TCFDictionary.ATT_TYPE, "line");
		docTextNodes.add(sSpan);
		
		//line3:
		spanTokens.clear();
		spanTokens.add(docTokens.get(5));
		spanTokens.add(docTokens.get(6));
		spanTokens.add(docTokens.get(7));
		spanTokens.add(docTokens.get(8));
		spanTokens.add(docTokens.get(9));
		spanTokens.add(docTokens.get(10));
		sSpan = docGraph.createSSpan(spanTokens);
		sSpan.createSAnnotation(TCFMapperImport.LAYER_TEXTSTRUCTURE, TCFDictionary.ATT_TYPE, "line");
		docTextNodes.add(sSpan);
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_TEXTSTRUCTURE);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		this.getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, false);
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, false);
		
		/* start mapper */
				
		this.getFixture().mapSDocument();
		
		
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = this.getFixture().getSDocument().getSDocumentGraph();
		assertEquals(1, fixGraph.getSLayerByName(TCFMapperImport.LAYER_TEXTSTRUCTURE).size());
		EList<SNode> fixTextNodes = fixGraph.getSLayerByName(TCFMapperImport.LAYER_TEXTSTRUCTURE).get(0).getSNodes();
		assertEquals(docTextNodes.size(), fixTextNodes.size());
		SNode docNode = null;
		SNode fixNode = null;
		for(int i=0; i<docTextNodes.size(); i++){
			docNode = docTextNodes.get(i);
			fixNode = fixTextNodes.get(i);
			assertTrue(fixNode instanceof SSpan);
			assertEquals(docNode.getSAnnotation(TCFMapperImport.LAYER_TEXTSTRUCTURE+"::"+TCFDictionary.ATT_TYPE), docNode.getSAnnotation(TCFMapperImport.LAYER_TEXTSTRUCTURE+"::"+TCFDictionary.ATT_TYPE));
			assertEquals(docGraph.getSText(docNode), fixGraph.getSText(fixNode));
		}
	}
}
