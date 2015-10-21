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
package org.corpus_tools.peppermodules.TCFModules.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.corpus_tools.peppermodules.tcfModules.TCFDictionary;
import org.corpus_tools.peppermodules.tcfModules.TCFImporterProperties;
import org.corpus_tools.peppermodules.tcfModules.TCFMapperImport;
import org.corpus_tools.salt.SALT_TYPE;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SPointingRelation;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SStructure;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.common.STextualRelation;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.core.SLayer;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.exceptions.SaltException;
import org.corpus_tools.salt.samples.SampleGenerator;
import org.corpus_tools.salt.semantics.SLemmaAnnotation;
import org.corpus_tools.salt.semantics.SPOSAnnotation;
import org.corpus_tools.salt.util.DiffOptions;
import org.corpus_tools.salt.util.Difference;
import org.corpus_tools.salt.util.SaltUtil;
import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		SDocument doc = SaltFactory.createSDocument();
		doc.setId("doc");
		doc.setDocumentGraph(SaltFactory.createSDocumentGraph());
		STextualDS primaryText = SaltFactory.createSTextualDS();
		primaryText.setText(EXAMPLE_TEXT);
		doc.getDocumentGraph().addNode(primaryText);
		
		
		/* setting variables*/
		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_PRIMARY_TEXT);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, SPAN_REUSE);
		
		/* start mapper */
				
		getFixture().mapSDocument();
		
		/* compare template salt model to imported salt model */
		assertNotNull(getFixture().getDocument());
		assertNotNull(getFixture().getDocument().getDocumentGraph());
		assertEquals(doc.getDocumentGraph().getTextualDSs().size(), getFixture().getDocument().getDocumentGraph().getTextualDSs().size());		
		assertEquals(doc.getDocumentGraph().getTextualDSs().get(0).getText().length(), getFixture().getDocument().getDocumentGraph().getTextualDSs().get(0).getText().length());
		assertEquals(doc.getDocumentGraph().getTextualDSs().get(0).getText(), getFixture().getDocument().getDocumentGraph().getTextualDSs().get(0).getText());		
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
		SDocument doc = SaltFactory.createSDocument();
		doc.setId("doc");
		doc.setDocumentGraph(SaltFactory.createSDocumentGraph());
		SampleGenerator.createPrimaryData(doc);
		SampleGenerator.createTokens(doc);
		SDocumentGraph docGraph = doc.getDocumentGraph();
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_TOKENS);
		tmpOut.getParentFile().mkdirs();//necessary? – is this test meant to be totally independent from the other one?
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, SPAN_REUSE);
		
		/* start mapper */
				
		getFixture().mapSDocument();
				
		
		/* test from testPrimaryData*/
		SDocumentGraph fixGraph = getFixture().getDocument().getDocumentGraph();
		
		assertNotNull(getFixture().getDocument());
		assertNotNull(getFixture().getDocument().getDocumentGraph());
		assertEquals(docGraph.getTextualDSs().size(), fixGraph.getTextualDSs().size());		
		assertEquals(docGraph.getTextualDSs().get(0).getText().length(), fixGraph.getTextualDSs().get(0).getText().length());
		assertEquals(docGraph.getTextualDSs().get(0).getText(), fixGraph.getTextualDSs().get(0).getText());	
		
		/* compare template salt model to imported salt model */
		
		List<SToken> docTokens = docGraph.getTokens();
		List<SToken> fixTokens = fixGraph.getTokens();
		
		assertNotEquals(fixGraph.getTextualDSs().size(), 0);
		assertNotEquals(fixTokens.size(), 0);
		assertEquals(docTokens.size(), fixTokens.size());		
		
		
		for(int i=0; i<docTokens.size(); i++){
			assertEquals(docGraph.getText(docTokens.get(i)), fixGraph.getText(fixTokens.get(i)));
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
		SDocument doc = SaltFactory.createSDocument();
		doc.setId("doc");
		doc.setDocumentGraph(SaltFactory.createSDocumentGraph());
		SDocumentGraph docGraph = doc.getDocumentGraph();
		docGraph.createTextualDS("I love New York.");
		docGraph.tokenize();
		
		SLayer posLayer = SaltFactory.createSLayer();
		posLayer.setName(TCFMapperImport.LAYER_POS);
		docGraph.addLayer(posLayer);
		posLayer.createMetaAnnotation(null, TCFDictionary.ATT_TAGSET, "penn treebank");
		
		SAnnotation anno = SaltFactory.createSPOSAnnotation();
		anno.setValue("PP");
		docGraph.getTokens().get(0).addAnnotation(anno);
		docGraph.getTokens().get(0).addLayer(posLayer);

		anno = SaltFactory.createSPOSAnnotation();
		anno.setValue("VBZ");
		docGraph.getTokens().get(1).addAnnotation(anno);
		posLayer.addNode(docGraph.getTokens().get(1));
		
		anno = SaltFactory.createSPOSAnnotation();
		anno.setValue("NNP");
		SSpan newYork = docGraph.createSpan(docGraph.getTokens().get(2));
		docGraph.addNode(newYork, docGraph.getTokens().get(3), SALT_TYPE.SSPANNING_RELATION);
		newYork.addAnnotation(anno);
		posLayer.addNode(newYork);
		
		anno = SaltFactory.createSPOSAnnotation();
		anno.setValue(".");
		docGraph.getTokens().get(4).addAnnotation(anno);
		posLayer.addNode(docGraph.getTokens().get(4));
		
		/**
		 * TODO SLayer <-- maybe put tagset information in there
		 */
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_TOKENS_POS);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, true);
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, SPAN_REUSE);
		
		/* start mapper */
				
		getFixture().mapSDocument();
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = getFixture().getDocument().getDocumentGraph();
		Set<Difference> diffs= docGraph.findDiffs(fixGraph);
		assertEquals(diffs.toString(), 0, diffs.size());
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
		SDocument doc = SaltFactory.createSDocument();
		doc.setId("doc");
		doc.setDocumentGraph(SaltFactory.createSDocumentGraph());
		SDocumentGraph docGraph = doc.getDocumentGraph();
		docGraph.createTextualDS("I love New York.");
		docGraph.tokenize();
		
		SLayer posLayer = SaltFactory.createSLayer();
		posLayer.setName(TCFMapperImport.LAYER_POS);
		
		SAnnotation anno = SaltFactory.createSPOSAnnotation();
		SSpan sSpan = docGraph.createSpan(docGraph.getTokens().get(0));
		anno.setValue("PP");
		sSpan.addAnnotation(anno);
		posLayer.addNode(sSpan);

		anno = SaltFactory.createSPOSAnnotation();
		sSpan = docGraph.createSpan(docGraph.getTokens().get(1));
		anno.setValue("VBZ");
		sSpan.addAnnotation(anno);
		posLayer.addNode(sSpan);
		
		anno = SaltFactory.createSPOSAnnotation();
		anno.setValue("NNP");
		sSpan = docGraph.createSpan(docGraph.getTokens().get(2));
		docGraph.addNode(sSpan, docGraph.getTokens().get(3), SALT_TYPE.SSPANNING_RELATION);
		sSpan.addAnnotation(anno);
		posLayer.addNode(sSpan);
		
		anno = SaltFactory.createSPOSAnnotation();
		sSpan = docGraph.createSpan(docGraph.getTokens().get(4));
		anno.setValue(".");
		sSpan.addAnnotation(anno);
		posLayer.addNode(sSpan);		
		
		/*TODO tagset information*/
		docGraph.addLayer(posLayer);
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_TOKENS_POS);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, false);
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, SPAN_REUSE);
						
		
		/* start mapper */
				
		getFixture().mapSDocument();
		
		/* compare template salt model to imported salt model */
		
		SDocumentGraph fixGraph = getFixture().getDocument().getDocumentGraph();
		SLayer fixLayer = fixGraph.getLayerByName(TCFMapperImport.LAYER_POS).get(0);
		String posQName = SaltUtil.createQName(SaltUtil.SALT_NAMESPACE, SaltUtil.SEMANTICS_POS);		
		
		assertEquals(docGraph.getSpans().size(), fixGraph.getSpans().size());
		assertEquals(fixGraph.getSpans().size(), docGraph.getTokens().size()-1);
		assertNotNull(fixLayer);
		assertEquals(posLayer.getNodes().size(), fixLayer.getNodes().size());
		for(int i=0; i<docGraph.getSpans().size(); i++){			
			assertNotNull(fixGraph.getSpans().get(i).getAnnotation(posQName));
			assertEquals(docGraph.getSpans().get(i).getAnnotation(posQName).getValue(), fixGraph.getSpans().get(i).getAnnotation(posQName).getValue());
			assertEquals(docGraph.getText(docGraph.getSpans().get(i)), fixGraph.getText(fixGraph.getSpans().get(i)));
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
		SDocument doc = SaltFactory.createSDocument();
		doc.setId("doc");
		doc.setDocumentGraph(SaltFactory.createSDocumentGraph());
		SampleGenerator.createPrimaryData(doc);
		SampleGenerator.createTokens(doc);	
		SDocumentGraph docGraph = doc.getDocumentGraph();
		SLayer sentenceLayer = SaltFactory.createSLayer();
		sentenceLayer.setName(TCFMapperImport.LAYER_SENTENCES);
		docGraph.removeLayer(docGraph.getLayers().iterator().next());
		docGraph.addLayer(sentenceLayer);

		/* adding sentence span */
		SSpan docSentence = docGraph.createSpan(docGraph.getTokens());
		docSentence.createAnnotation(null, TCFMapperImport.LEVEL_SENTENCE, TCFMapperImport.LEVEL_SENTENCE);
		sentenceLayer.addNode(docSentence);

		/* setting variables */	
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_SENTENCE);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);	
		p.println(outStream.toString());
		p.close();
		getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, SPAN_REUSE);
		
		/* start mapper */
				
		getFixture().mapSDocument();
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = getFixture().getDocument().getDocumentGraph();
		Set<Difference> diffs= docGraph.findDiffs(fixGraph);
		assertEquals(diffs.toString(), 0, diffs.size());	
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
		SDocument doc = SaltFactory.createSDocument();
		doc.setId("doc");
		doc.setDocumentGraph(SaltFactory.createSDocumentGraph());		
		SDocumentGraph docGraph = doc.getDocumentGraph();
		docGraph.createTextualDS(EXAMPLE_TEXT_SHRINK);
		docGraph.tokenize();
		/*TODO add lemma layer in salt sample*/
		SLayer docLemmaLayer = SaltFactory.createSLayer();
		docLemmaLayer.setName(TCFMapperImport.LAYER_LEMMA);
		List<SToken> docTokens = docGraph.getTokens();
		docGraph.addLayer(docLemmaLayer);
		
		SNode sNode = docTokens.get(0);
		SAnnotation sAnno = SaltFactory.createSLemmaAnnotation();
		sAnno.setValue("I");
		sNode.addAnnotation(sAnno);
		docLemmaLayer.addNode(sNode);
		
		sNode = docTokens.get(1);
		sAnno = SaltFactory.createSLemmaAnnotation();
		sAnno.setValue("love");
		sNode.addAnnotation(sAnno);
		docLemmaLayer.addNode(sNode);
		
		sNode = docGraph.createSpan(docTokens.get(2));
		docGraph.addNode(sNode, docTokens.get(3), SALT_TYPE.SSPANNING_RELATION);
		sAnno = SaltFactory.createSLemmaAnnotation();
		sAnno.setValue("New York");
		sNode.addAnnotation(sAnno);
		docLemmaLayer.addNode(sNode);
		
		sNode = docTokens.get(4);
		sAnno = SaltFactory.createSLemmaAnnotation();
		sAnno.setValue(".");
		sNode.addAnnotation(sAnno);
		docLemmaLayer.addNode(sNode);		
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_TOKENS_LEMMA);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, true);
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, SPAN_REUSE);
		
		/* start mapper */
				
		getFixture().mapSDocument();
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = getFixture().getDocument().getDocumentGraph();
		Set<Difference> diffs= docGraph.findDiffs(fixGraph);
		assertEquals(diffs.toString(), 0, diffs.size());
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
		SDocument doc = SaltFactory.createSDocument();
		doc.setId("doc");
		doc.setDocumentGraph(SaltFactory.createSDocumentGraph());		
		SDocumentGraph docGraph = doc.getDocumentGraph();
		docGraph.createTextualDS(EXAMPLE_TEXT_SHRINK);
		docGraph.tokenize();
		List<SToken> docTokens = docGraph.getTokens();
		SLayer docLemmaLayer = SaltFactory.createSLayer();
		docLemmaLayer.setName(TCFMapperImport.LAYER_LEMMA);
		docGraph.addLayer(docLemmaLayer);
		
		SSpan sSpan = docGraph.createSpan(docTokens.get(0));
		SAnnotation sAnno = SaltFactory.createSLemmaAnnotation();
		sAnno.setValue("I");
		sSpan.addAnnotation(sAnno);
		sSpan.addLayer(docLemmaLayer);
		
		sSpan = docGraph.createSpan(docTokens.get(1));
		sAnno = SaltFactory.createSLemmaAnnotation();
		sAnno.setValue("love");
		sSpan.addAnnotation(sAnno);
		sSpan.addLayer(docLemmaLayer);
		
		sSpan = docGraph.createSpan(docTokens.get(2));
		docGraph.addNode(sSpan, docTokens.get(3), SALT_TYPE.SSPANNING_RELATION);
		sAnno = SaltFactory.createSLemmaAnnotation();
		sAnno.setValue("New York");
		sSpan.addAnnotation(sAnno);
		sSpan.addLayer(docLemmaLayer);
		
		sSpan = docGraph.createSpan(docTokens.get(4));
		sAnno = SaltFactory.createSLemmaAnnotation();
		sAnno.setValue(".");
		sSpan.addAnnotation(sAnno);
		sSpan.addLayer(docLemmaLayer);		
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_TOKENS_LEMMA);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, false);
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, SPAN_REUSE);
		
		/* start mapper */
				
		getFixture().mapSDocument();
		
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = getFixture().getDocument().getDocumentGraph();
		Set<Difference> diffs= docGraph.findDiffs(fixGraph);
		assertEquals(diffs.toString(), 0, diffs.size());
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
		SDocument doc = SaltFactory.createSDocument();
		doc.setId("doc");
		doc.setDocumentGraph(SaltFactory.createSDocumentGraph());
		SampleGenerator.createPrimaryData(doc);
		SampleGenerator.createTokens(doc);
		SampleGenerator.createDependencies(doc);
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_DEPENDENCIES_NO_MULTIGOVS);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, SPAN_REUSE);
		
		/* start mapper */
				
		getFixture().mapSDocument();
				
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
		SDocumentGraph fixGraph = getFixture().getDocument().getDocumentGraph();
		SDocumentGraph docGraph = doc.getDocumentGraph();
		List<SPointingRelation> docPRels = docGraph.getPointingRelations();
		List<SPointingRelation> fixPRels = fixGraph.getPointingRelations();		
		
		assertNotNull(fixPRels);
		assertNotEquals(0, fixPRels.size());		
		assertEquals(docPRels.size(), fixPRels.size());
		for(int i=0; i<docPRels.size(); i++){
			assertNotNull(docPRels.get(i).getSource());
			assertNotNull(docPRels.get(i).getTarget());
			assertNotNull(fixPRels.get(i).getSource());
			assertNotNull(fixPRels.get(i).getTarget());
		}
		
		/* collect all tokens + their String values */
		
		EMap<SToken, String> docTokensText = new BasicEMap<SToken, String>();
		EMap<SToken, String> fixTokensText = new BasicEMap<SToken, String>();
		List<SToken> orderedFixTokens = new ArrayList<SToken>();		
		
		SToken sTok = null;
		for(STextualRelation txtRel : docGraph.getTextualRelations()){			
			sTok = (SToken)txtRel.getSource();
			docTokensText.put(sTok, docGraph.getTextualDSs().get(0).getText().substring(txtRel.getStart(), txtRel.getEnd()));
		}
		
		for(STextualRelation txtRel : fixGraph.getTextualRelations()){
			sTok = (SToken)txtRel.getSource();
			orderedFixTokens.add(sTok);
			fixTokensText.put(sTok, fixGraph.getTextualDSs().get(0).getText().substring(txtRel.getStart(), txtRel.getEnd()));			
		}		
		
		/* check dependencies
		 * 
		 * we have to do it in the linear order of the tokens,
		 * because the doubling of both tokens (String) AND
		 * dependencies (String) is possible 
		 * */
		SToken fixTok = null;
		for(SToken docTok : docGraph.getSortedTokenByText()){
			/* find SPointingRelation for docTok */
			int j = 0;
			boolean isGoverned = true;
			while(!docPRels.get(j).getTarget().equals(docTok)){
				assertNotNull(docPRels.get(j).getTarget());
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
				while(!fixPRels.get(k).getTarget().equals(fixTok)){
					assertNotNull(fixPRels.get(k).getTarget());
					k++;					
				}
				/* check dependency */
				assertEquals(docPRels.get(j).getAnnotation("dependency").getValue(), fixPRels.get(j).getAnnotation(TCFMapperImport.LAYER_DEPENDENCIES+"::"+TCFDictionary.ATT_FUNC).getValue());				
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
		SDocument doc = SaltFactory.createSDocument();
		doc.setId("doc");
		doc.setDocumentGraph(SaltFactory.createSDocumentGraph());
		SampleGenerator.createPrimaryData(doc);
		SampleGenerator.createTokens(doc);
		/* TODO create Version 2 of both the following methods --> Root node necessary to include punctuation */
		/* think about some kind of flag to include/exclude the question mark */
		SampleGenerator.createSyntaxStructure(doc);
		SampleGenerator.createSyntaxAnnotations(doc);
		SDocumentGraph docGraph = doc.getDocumentGraph();
		SAnnotation anno = null;
		for (SNode node : docGraph.getNodes()){
			anno = node.getAnnotation("const");
			if (anno!=null){
				anno.setNamespace(TCFMapperImport.LAYER_CONSTITUENTS);
				anno.setName(TCFDictionary.ATT_CAT);
			}
		}
		
		List<SLayer> rm = new ArrayList<SLayer>();
		for (SLayer layer : docGraph.getLayers()){
			if (SampleGenerator.SYNTAX_LAYER.equals(layer.getName())){
				layer.setName(TCFMapperImport.LAYER_CONSTITUENTS);
				layer.createMetaAnnotation(null, TCFDictionary.ATT_TAGSET, "unknown");
			} else {
				rm.add(layer);
			}
		}
		for (SLayer l : rm){
			docGraph.removeLayer(l);
		}
		
		String[] tokenCats = {"VBZ","DT","NN","RBR","JJ","IN","PRP","VBZ","TO","VB","."};
		int j=0;
		for (SToken tok : docGraph.getSortedTokenByText()){
			tok.createAnnotation(TCFMapperImport.LAYER_CONSTITUENTS, TCFDictionary.ATT_CAT, tokenCats[j]);
			j++;
		}
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_CONSTITUENT_PARSING);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, SPAN_REUSE);
		
		/* start mapper */
				
		getFixture().mapSDocument();
		
		/* -- compare template salt model to imported salt model -- */
		SDocumentGraph fixGraph = getFixture().getDocument().getDocumentGraph();
		
		Set<Difference> diffs= docGraph.findDiffs(fixGraph);
		assertEquals(diffs.toString(), 0, diffs.size());	
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
		SDocument doc = SaltFactory.createSDocument();
		doc.setId("doc");
		doc.setDocumentGraph(SaltFactory.createSDocumentGraph());
		SDocumentGraph docGraph = doc.getDocumentGraph();
		docGraph.createTextualDS(EXAMPLE_TEXT_SHRINK);
		docGraph.tokenize();
		List<SToken> docTokens = docGraph.getTokens();
		SLayer docSynLayer = SaltFactory.createSLayer(); 
		docSynLayer.createMetaAnnotation(null, TCFDictionary.ATT_TAGSET, "unknown");
		docGraph.addLayer(docSynLayer);
		docSynLayer.setName(TCFMapperImport.LAYER_CONSTITUENTS);	
		
		SStructure root = SaltFactory.createSStructure();//ROOT
		root.createAnnotation(TCFMapperImport.LAYER_CONSTITUENTS, TCFDictionary.ATT_CAT, "ROOT");
		SStructure s = SaltFactory.createSStructure();//S
		s.createAnnotation(TCFMapperImport.LAYER_CONSTITUENTS, TCFDictionary.ATT_CAT, "S");
		SStructure np1 = SaltFactory.createSStructure();//NP(1)
		np1.createAnnotation(TCFMapperImport.LAYER_CONSTITUENTS, TCFDictionary.ATT_CAT, "NP");
		SStructure np2 = SaltFactory.createSStructure();//NP(2)
		np2.createAnnotation(TCFMapperImport.LAYER_CONSTITUENTS, TCFDictionary.ATT_CAT, "NP");
		SSpan newYork = docGraph.createSpan(docTokens.get(2));
		docGraph.addNode(newYork, docTokens.get(3), SALT_TYPE.SSPANNING_RELATION);
		newYork.createAnnotation(TCFMapperImport.LAYER_CONSTITUENTS, TCFDictionary.ATT_CAT, "NNP");
		
		docGraph.addNode(root);
		docGraph.addNode(root, s, SALT_TYPE.SDOMINANCE_RELATION);
		docGraph.addNode(s, np1, SALT_TYPE.SDOMINANCE_RELATION);
		docGraph.addNode(np1, docTokens.get(0), SALT_TYPE.SDOMINANCE_RELATION);
		docGraph.addNode(s, docTokens.get(1), SALT_TYPE.SDOMINANCE_RELATION);
		docGraph.addNode(s, np2, SALT_TYPE.SDOMINANCE_RELATION);
		docGraph.addNode(np2, newYork, SALT_TYPE.SDOMINANCE_RELATION);
		docGraph.addNode(root, docTokens.get(4), SALT_TYPE.SDOMINANCE_RELATION);
		
		root.addLayer(docSynLayer);
		s.addLayer(docSynLayer);
		np1.addLayer(docSynLayer);
		np2.addLayer(docSynLayer);
		
		docTokens.get(0).createAnnotation(TCFMapperImport.LAYER_CONSTITUENTS, TCFDictionary.ATT_CAT, "PP");
		docTokens.get(1).createAnnotation(TCFMapperImport.LAYER_CONSTITUENTS, TCFDictionary.ATT_CAT, "VBZ");
		docTokens.get(4).createAnnotation(TCFMapperImport.LAYER_CONSTITUENTS, TCFDictionary.ATT_CAT, ".");
		/* spans and tokens do not belong to the constituent layer */
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_CONSTITUENT_PARSING);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, true);
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, SPAN_REUSE);
		
		/* start mapper */
				
		getFixture().mapSDocument();
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = getFixture().getDocument().getDocumentGraph();
		Set<Difference> diffs= docGraph.findDiffs(fixGraph);
		assertEquals(diffs.toString(), 0, diffs.size());
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
		SDocument doc = SaltFactory.createSDocument();
		doc.setId("doc");
		doc.setDocumentGraph(SaltFactory.createSDocumentGraph());
		SDocumentGraph docGraph = doc.getDocumentGraph();
		docGraph.createTextualDS(EXAMPLE_TEXT_SHRINK);
		docGraph.tokenize();
		List<SToken> docTokens = docGraph.getTokens();
		SLayer docSynLayer = SaltFactory.createSLayer();
		docSynLayer.createMetaAnnotation(null, TCFDictionary.ATT_TAGSET, "unknown");
		docGraph.addLayer(docSynLayer);
		docSynLayer.setName(TCFMapperImport.LAYER_CONSTITUENTS);
//		Set<SNode> docConstituents = docSynLayer.getNodes();	
		
		SSpan sI = docGraph.createSpan(docTokens.get(0));
		sI.createAnnotation(TCFMapperImport.LAYER_CONSTITUENTS, TCFDictionary.ATT_CAT, "PP");
		
		SSpan sLove = docGraph.createSpan(docTokens.get(1));
		sLove.createAnnotation(TCFMapperImport.LAYER_CONSTITUENTS, TCFDictionary.ATT_CAT, "VBZ");
		
		SSpan sNewYork = docGraph.createSpan(docTokens.get(2));		
		docGraph.addNode(sNewYork, docTokens.get(3), SALT_TYPE.SSPANNING_RELATION);
		sNewYork.createAnnotation(TCFMapperImport.LAYER_CONSTITUENTS, TCFDictionary.ATT_CAT, "NNP");
		
		SSpan sStop = docGraph.createSpan(docTokens.get(4));
		sStop.createAnnotation(TCFMapperImport.LAYER_CONSTITUENTS, TCFDictionary.ATT_CAT, ".");
		
		SStructure root = SaltFactory.createSStructure();//ROOT
		root.createAnnotation(TCFMapperImport.LAYER_CONSTITUENTS, TCFDictionary.ATT_CAT, "ROOT");
		SStructure s = SaltFactory.createSStructure();//S
		s.createAnnotation(TCFMapperImport.LAYER_CONSTITUENTS, TCFDictionary.ATT_CAT, "S");
		SStructure np1 = SaltFactory.createSStructure();//NP(1)
		np1.createAnnotation(TCFMapperImport.LAYER_CONSTITUENTS, TCFDictionary.ATT_CAT, "NP");
		SStructure np2 = SaltFactory.createSStructure();//NP(2)
		np2.createAnnotation(TCFMapperImport.LAYER_CONSTITUENTS, TCFDictionary.ATT_CAT, "NP");		
		
		docGraph.addNode(root);
		docGraph.addNode(root, s, SALT_TYPE.SDOMINANCE_RELATION);
		docGraph.addNode(s, np1, SALT_TYPE.SDOMINANCE_RELATION);
		docGraph.addNode(np1, sI, SALT_TYPE.SDOMINANCE_RELATION);
		docGraph.addNode(s, sLove, SALT_TYPE.SDOMINANCE_RELATION);
		docGraph.addNode(s, np2, SALT_TYPE.SDOMINANCE_RELATION);
		docGraph.addNode(np2, sNewYork, SALT_TYPE.SDOMINANCE_RELATION);
		docGraph.addNode(root, sStop, SALT_TYPE.SDOMINANCE_RELATION);
		
		root.addLayer(docSynLayer);
		s.addLayer(docSynLayer);
		np1.addLayer(docSynLayer);
		np2.addLayer(docSynLayer);
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_CONSTITUENT_PARSING);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, false);
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, SPAN_REUSE);
		
		/* start mapper */
				
		getFixture().mapSDocument();
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = getFixture().getDocument().getDocumentGraph();
		Set<Difference> diffs= docGraph.findDiffs(fixGraph);
		assertEquals(diffs.toString(), 0, diffs.size());	
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
		SDocument doc = SaltFactory.createSDocument();
		doc.setId("doc");
		doc.setDocumentGraph(SaltFactory.createSDocumentGraph());
		SampleGenerator.createPrimaryData(doc);
		SampleGenerator.createTokens(doc);		
		
		/* adding morphological annotation manually to salt sample */
		/* TODO add to salt sample (Florian okay)*/
		SDocumentGraph docGraph = doc.getDocumentGraph();
		List<SToken> docTokens = docGraph.getSortedTokenByText();
		SLayer docMorphLayer = SaltFactory.createSLayer();
		docMorphLayer.setName(TCFMapperImport.LAYER_TCF_MORPHOLOGY);
		docGraph.removeLayer(docGraph.getLayers().iterator().next());
		docGraph.addLayer(docMorphLayer);

		SSpan sSpan = docGraph.createSpan(docTokens.get(0));//Is
		sSpan.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "cat", "verb");
		sSpan.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "person", "3");
		sSpan.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "number", "singular");
		sSpan.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "tense", "present");
		sSpan.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "indicative", "true");
		sSpan.createAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
		//in this last annotation (storage of segment.type) we use a namespace to avoid ambiguities
		//with potential morphological properties used in <analysis>...</analysis> (therefore namespace = TAG_TC_SEGMENT)
//		sSpan.createAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "be");
		//the character sequence between <segment>...</segment> will be represented in the same namespace and with key=TAG(=namespace)
		//I'm not sure I like that
		docMorphLayer.addNode(sSpan);
		
		sSpan = docGraph.createSpan(docTokens.get(1));//this
		sSpan.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "cat", "determiner");
		sSpan.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "number", "singular");
		sSpan.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "definiteness", "true");
		sSpan.createAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
		docMorphLayer.addNode(sSpan);
		
		sSpan = docGraph.createSpan(docTokens.get(2));//example
		sSpan.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "cat", "noun");
		sSpan.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "number", "singular");
		sSpan.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "gender", "neuter");
		sSpan.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "case", "nominative");
		sSpan.createAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
//		sSpan.createAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "example");
		docMorphLayer.addNode(sSpan);
		
		List<SToken> spanTokens = new ArrayList<SToken>();
		spanTokens.add(docTokens.get(3));
		spanTokens.add(docTokens.get(4));
		
		sSpan = docGraph.createSpan(spanTokens);//more complicated		
		sSpan.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "cat", "adjective");
		sSpan.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "comparative", "true");
		sSpan.createAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
//		sSpan.createAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "complicated");
		docMorphLayer.addNode(sSpan);
		
		spanTokens.clear();
		
		sSpan = docGraph.createSpan(docTokens.get(5));//than
		sSpan.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "cat", "conjunction");
		sSpan.createAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
//		sSpan.createAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "than");
		docMorphLayer.addNode(sSpan);		
		
		sSpan = docGraph.createSpan(docTokens.get(6));//it
		sSpan.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "cat", "personal pronoun");
		sSpan.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "number", "singular");
		sSpan.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "person", "3");
		sSpan.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "gender", "neuter");
		sSpan.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "case", "nominative");
		sSpan.createAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
//		sSpan.createAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "it");
		docMorphLayer.addNode(sSpan);
		
		sSpan = docGraph.createSpan(docTokens.get(7));//appears
		sSpan.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "cat", "verb");
		sSpan.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "person", "3");
		sSpan.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "number", "singular");
		sSpan.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "tense", "present");
		sSpan.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "indicative", "true");
		sSpan.createAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
//		sSpan.createAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "appear");
		docMorphLayer.addNode(sSpan);
		
		spanTokens.add(docTokens.get(8));
		spanTokens.add(docTokens.get(9));
		
		sSpan = docGraph.createSpan(spanTokens);//to be
		sSpan.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "cat", "verb");
		sSpan.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "infinitive", "true");
		sSpan.createAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
//		sSpan.createAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "be");
		docMorphLayer.addNode(sSpan);
		
		spanTokens.clear();
		
		sSpan = docGraph.createSpan(docTokens.get(10));//?
		sSpan.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "cat", "punctuation");
		sSpan.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "punctuation", "question");
		sSpan.createAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
//		sSpan.createAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, ".");
		docMorphLayer.addNode(sSpan);
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_MORPHOLOGY);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, false);
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, SPAN_REUSE);
		
		/* start mapper */
				
		getFixture().mapSDocument();
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = getFixture().getDocument().getDocumentGraph();
		Set<Difference> diffs= docGraph.findDiffs(fixGraph);
		assertEquals(diffs.toString(), 0, diffs.size());
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
		SDocument doc = SaltFactory.createSDocument();
		doc.setId("doc");
		doc.setDocumentGraph(SaltFactory.createSDocumentGraph());
		SampleGenerator.createPrimaryData(doc);
		SampleGenerator.createTokens(doc);		
		
		/* adding morphological annotation manually to salt sample */
		/* TODO add to salt sample (Florian okay)*/
		SDocumentGraph docGraph = doc.getDocumentGraph();
		List<SToken> docTokens = docGraph.getSortedTokenByText();
		SLayer docMorphLayer = SaltFactory.createSLayer();
		docMorphLayer.setName(TCFMapperImport.LAYER_TCF_MORPHOLOGY);
		docGraph.removeLayer(docGraph.getLayers().iterator().next());
		docGraph.addLayer(docMorphLayer);
		
		SNode sNode = docTokens.get(0);
		
		sNode.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "cat", "verb");//Is
		sNode.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "person", "3");
		sNode.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "number", "singular");
		sNode.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "tense", "present");
		sNode.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "indicative", "true");
		sNode.createAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");//
		//in this last annotation (storage of segment.type) we use a namespace to avoid ambiguities
		//with potential morphological properties used in <analysis>...</analysis> (therefore namespace = TAG_TC_SEGMENT)
//		sNode.createAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "be");//
		//the character sequence between <segment>...</segment> will be represented in the same namespace and with key=TAG(=namespace)
		//I'm not sure I like that
		docMorphLayer.addNode(sNode);
		
		sNode = docTokens.get(1);//this
		sNode.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "cat", "determiner");
		sNode.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "number", "singular");
		sNode.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "definiteness", "true");
		sNode.createAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");//
		docMorphLayer.addNode(sNode);
		
		sNode = docTokens.get(2);//example
		sNode.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "cat", "noun");
		sNode.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "number", "singular");
		sNode.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "gender", "neuter");
		sNode.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "case", "nominative");
		sNode.createAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");//
//		sNode.createAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "example");//
		docMorphLayer.addNode(sNode);
		
		List<SToken> spanTokens = new ArrayList<SToken>();
		spanTokens.add(docTokens.get(3));
		spanTokens.add(docTokens.get(4));
		
		sNode = docGraph.createSpan(spanTokens);//more complicated		
		sNode.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "cat", "adjective");
		sNode.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "comparative", "true");
		sNode.createAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");//
//		sNode.createAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "complicated");//
		docMorphLayer.addNode(sNode);
		
		spanTokens.clear();
		
		sNode = docTokens.get(5);//than
		sNode.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "cat", "conjunction");
		sNode.createAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");//
//		sNode.createAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "than");//
		docMorphLayer.addNode(sNode);		
		
		sNode = docTokens.get(6);//it
		sNode.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "cat", "personal pronoun");
		sNode.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "number", "singular");
		sNode.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "person", "3");
		sNode.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "gender", "neuter");
		sNode.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "case", "nominative");
		sNode.createAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");//
//		sNode.createAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "it");//
		docMorphLayer.addNode(sNode);
		
		sNode = docTokens.get(7);//appears
		sNode.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "cat", "verb");
		sNode.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "person", "3");
		sNode.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "number", "singular");
		sNode.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "tense", "present");
		sNode.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "indicative", "true");
		sNode.createAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");//
//		sNode.createAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "appear");//
		docMorphLayer.addNode(sNode);
		
		spanTokens.add(docTokens.get(8));
		spanTokens.add(docTokens.get(9));
		
		sNode = docGraph.createSpan(spanTokens);//to be
		sNode.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "cat", "verb");
		sNode.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "infinitive", "true");
		sNode.createAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");//
//		sNode.createAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "be");//
		docMorphLayer.addNode(sNode);
		
		spanTokens.clear();
		
		sNode = docTokens.get(10);//?
		sNode.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "cat", "punctuation");
		sNode.createAnnotation(TCFMapperImport.LAYER_TCF_MORPHOLOGY, "punctuation", "question");
		sNode.createAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");//
//		sNode.createAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, ".");//
		docMorphLayer.addNode(sNode);
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_MORPHOLOGY);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, true);
		
		/* start mapper */
				
		getFixture().mapSDocument();
		SDocumentGraph fixGraph = getFixture().getDocument().getDocumentGraph();
		
				
		/* compare template salt model to imported salt model */		
		Set<Difference> diffs= docGraph.findDiffs(fixGraph);
		assertEquals(diffs.toString(), 0, diffs.size());
//		
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
		SDocument doc = SaltFactory.createSDocument();
		doc.setId("doc");
		SDocumentGraph docGraph = SaltFactory.createSDocumentGraph();
		doc.setDocumentGraph(docGraph);
		docGraph.createTextualDS(EXAMPLE_TEXT_NAMED_ENTITIES);
		docGraph.tokenize();
		List<SToken> docTokens = docGraph.getTokens();
		SLayer docNELayer = SaltFactory.createSLayer();
		docNELayer.setName(TCFMapperImport.LAYER_NE);
		docGraph.addLayer(docNELayer);
		
		//add tokens to NE-layer and annotate them as named entities; add tag set (type) information to layer
		docTokens.get(0).createAnnotation(TCFMapperImport.LAYER_NE, TCFDictionary.ATT_CLASS, "person");		
		docNELayer.addNode(docTokens.get(0));
		
		SSpan newYork = docGraph.createSpan(docTokens.get(2));
		docGraph.addNode(newYork, docTokens.get(3), SALT_TYPE.SSPANNING_RELATION);
		newYork.createAnnotation(TCFMapperImport.LAYER_NE, TCFDictionary.ATT_CLASS, "location");
		docNELayer.addNode(newYork);
		
		docTokens.get(8).createAnnotation(TCFMapperImport.LAYER_NE, TCFDictionary.ATT_CLASS, "location");
		docNELayer.addNode(docTokens.get(8));
		
		docNELayer.createMetaAnnotation(null, TCFDictionary.ATT_TYPE, "anySet");
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_NE);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, true);
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, false);
		
		/* start mapper */
				
		getFixture().mapSDocument();
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = getFixture().getDocument().getDocumentGraph();
		Set<Difference> diffs= docGraph.findDiffs(fixGraph);
		assertEquals(diffs.toString(), 0, diffs.size());
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
		SDocument doc = SaltFactory.createSDocument();
		doc.setId("doc");
		SDocumentGraph docGraph = SaltFactory.createSDocumentGraph();
		doc.setDocumentGraph(docGraph);
		docGraph.createTextualDS(EXAMPLE_TEXT_NAMED_ENTITIES);
		docGraph.tokenize();
		List<SToken> docTokens = docGraph.getTokens();
		SLayer docNELayer = SaltFactory.createSLayer();
		docNELayer.setName(TCFMapperImport.LAYER_NE);
		docGraph.addLayer(docNELayer);
		
		//add tokens to NE-layer and annotate them as named entities; add tag set (type) information to layer
		SSpan ne = docGraph.createSpan(docTokens.get(0));
		ne.createAnnotation(TCFMapperImport.LAYER_NE, TCFDictionary.ATT_CLASS, "person");		
		docNELayer.addNode(ne);
		
		ne = docGraph.createSpan(docTokens.get(2));
		docGraph.addNode(ne, docTokens.get(3), SALT_TYPE.SSPANNING_RELATION);
		ne.createAnnotation(TCFMapperImport.LAYER_NE, TCFDictionary.ATT_CLASS, "location");
		docNELayer.addNode(ne);
		
		ne = docGraph.createSpan(docTokens.get(8));
		ne.createAnnotation(TCFMapperImport.LAYER_NE, TCFDictionary.ATT_CLASS, "location");
		docNELayer.addNode(ne);
		
		docNELayer.createMetaAnnotation(null, TCFDictionary.ATT_TYPE, "anySet");
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_NE);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, false);
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, false);
		
		/* start mapper */
				
		getFixture().mapSDocument();
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = getFixture().getDocument().getDocumentGraph();
		Set<Difference> diffs= docGraph.findDiffs(fixGraph);
		assertEquals(diffs.toString(), 0, diffs.size());
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
		SDocument doc = SaltFactory.createSDocument();
		doc.setId("doc");
		SDocumentGraph docGraph = SaltFactory.createSDocumentGraph();
		doc.setDocumentGraph(docGraph);
		docGraph.createTextualDS(EXAMPLE_TEXT_REFERENCE);
		docGraph.tokenize();
		List<SToken> docTokens = docGraph.getTokens();
		
		//reference layer:
		SLayer docRefLayer = SaltFactory.createSLayer();
		docRefLayer.setName(TCFMapperImport.LAYER_REFERENCES);
		docRefLayer.createMetaAnnotation(null, TCFDictionary.ATT_TYPETAGSET, "unknown");
		docRefLayer.createMetaAnnotation(null, TCFDictionary.ATT_RELTAGSET, "unknown");
		docGraph.addLayer(docRefLayer);
		
		//relation:
		SPointingRelation reference = SaltFactory.createSPointingRelation();
		
		//entity "New York":
		///"New York"
		SSpan newYork = docGraph.createSpan(docTokens.get(2));
		docGraph.addNode(newYork, docTokens.get(3), SALT_TYPE.SSPANNING_RELATION);
		newYork.createAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_TYPE, "name");
		docRefLayer.addNode(newYork);		
		///"the most beautiful place"
		SSpan theMostBeautifulPlace = docGraph.createSpan(docTokens.get(7));
		docGraph.addNode(theMostBeautifulPlace, docTokens.get(8), SALT_TYPE.SSPANNING_RELATION);
		docGraph.addNode(theMostBeautifulPlace, docTokens.get(9), SALT_TYPE.SSPANNING_RELATION);
		docGraph.addNode(theMostBeautifulPlace, docTokens.get(10), SALT_TYPE.SSPANNING_RELATION);		
		theMostBeautifulPlace.createAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_TYPE, "noun");
		reference.setType("reference");
		reference.setSource(theMostBeautifulPlace);
		reference.setTarget(newYork);
		reference.createAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_REL, "non-anaphoric");
		docRefLayer.addNode(theMostBeautifulPlace);
		docRefLayer.addRelation(reference);
		///"it"
		docTokens.get(5).createAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_TYPE, "pronoun");
		reference = SaltFactory.createSPointingRelation();
		reference.setType("reference");
		reference.setSource(docTokens.get(5));
		reference.setTarget(newYork);
		reference.createAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_REL, "anaphoric");
		docRefLayer.addNode(docTokens.get(5));
		docRefLayer.addRelation(reference);
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_REFERENCES);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, true);
		
		/* start mapper */
				
		getFixture().mapSDocument();
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = getFixture().getDocument().getDocumentGraph();
		Set<Difference> diffs= docGraph.findDiffs(fixGraph);
		assertEquals(diffs.toString(), 0, diffs.size());
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
		SDocument doc = SaltFactory.createSDocument();
		doc.setId("doc");
		SDocumentGraph docGraph = SaltFactory.createSDocumentGraph();
		doc.setDocumentGraph(docGraph);
		docGraph.createTextualDS(EXAMPLE_TEXT_REFERENCE);
		docGraph.tokenize();
		List<SToken> docTokens = docGraph.getTokens();
		
		//reference layer:
		SLayer docRefLayer = SaltFactory.createSLayer();
		docRefLayer.setName(TCFMapperImport.LAYER_REFERENCES);
		docRefLayer.createMetaAnnotation(null, TCFDictionary.ATT_TYPETAGSET, "unknown");
		docRefLayer.createMetaAnnotation(null, TCFDictionary.ATT_RELTAGSET, "unknown");
		docGraph.addLayer(docRefLayer);
		
		//relation:
		SPointingRelation reference = SaltFactory.createSPointingRelation();
		
		//entity "New York":
		///"New York"
		SSpan newYork = docGraph.createSpan(docTokens.get(2));
		docGraph.addNode(newYork, docTokens.get(3), SALT_TYPE.SSPANNING_RELATION);
		newYork.createAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_TYPE, "name");
		docRefLayer.addNode(newYork);		
		///"the most beautiful place"
		SSpan theMostBeautifulPlace = docGraph.createSpan(docTokens.get(7));
		docGraph.addNode(theMostBeautifulPlace, docTokens.get(8), SALT_TYPE.SSPANNING_RELATION);
		docGraph.addNode(theMostBeautifulPlace, docTokens.get(9), SALT_TYPE.SSPANNING_RELATION);
		docGraph.addNode(theMostBeautifulPlace, docTokens.get(10), SALT_TYPE.SSPANNING_RELATION);		
		theMostBeautifulPlace.createAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_TYPE, "noun");	
		reference = (SPointingRelation)docGraph.addNode(theMostBeautifulPlace, newYork, SALT_TYPE.SPOINTING_RELATION);
		reference.createAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_REL, "non-anaphoric");
		reference.setType("reference");
		docRefLayer.addNode(theMostBeautifulPlace);
		docRefLayer.addRelation(reference);
		///"it"
		docTokens.get(5).createAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_TYPE, "pronoun");
		reference = (SPointingRelation)docGraph.addNode(docTokens.get(5), newYork, SALT_TYPE.SPOINTING_RELATION);
		reference.setType("reference");
		reference.createAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_REL, "anaphoric");
		docRefLayer.addNode(docTokens.get(5));
		docRefLayer.addRelation(reference);
		
		//test entity
		///love
		docTokens.get(1).createAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_TYPE, "test");
		docRefLayer.addNode(docTokens.get(1));
		///is
		docTokens.get(6).createAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_TYPE, "test");
		docRefLayer.addNode(docTokens.get(6));
		///relation:
		reference = (SPointingRelation)docGraph.addNode(docTokens.get(1), docTokens.get(6), SALT_TYPE.SPOINTING_RELATION);
		reference.setType("reference");
		reference.createAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_REL, "non-anaphoric");
		docRefLayer.addRelation(reference);
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_REFERENCES);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, true);
		
		/* start mapper */
				
		getFixture().mapSDocument();
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = getFixture().getDocument().getDocumentGraph();
		Set<Difference> diffs= docGraph.findDiffs(fixGraph);
		assertEquals(diffs.toString(), 0, diffs.size());
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
		SDocument doc = SaltFactory.createSDocument();
		doc.setId("doc");
		SDocumentGraph docGraph = SaltFactory.createSDocumentGraph();
		doc.setDocumentGraph(docGraph);
		docGraph.createTextualDS(EXAMPLE_TEXT_REFERENCE);
		docGraph.tokenize();
		List<SToken> docTokens = docGraph.getTokens();
		
		//reference layer:
		SLayer docRefLayer = SaltFactory.createSLayer();
		docRefLayer.setName(TCFMapperImport.LAYER_REFERENCES);
		docRefLayer.createMetaAnnotation(null, TCFDictionary.ATT_TYPETAGSET, "unknown");
		docRefLayer.createMetaAnnotation(null, TCFDictionary.ATT_RELTAGSET, "unknown");
		docGraph.addLayer(docRefLayer);
		
		//relation:
		SPointingRelation reference = SaltFactory.createSPointingRelation();
		
		//entity "New York":
		///"New York"
		SSpan newYork = docGraph.createSpan(docTokens.get(2));
		docGraph.addNode(newYork, docTokens.get(3), SALT_TYPE.SSPANNING_RELATION);
		newYork.createAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_TYPE, "name");
		docRefLayer.addNode(newYork);		
		///"the most beautiful place"
		SSpan theMostBeautifulPlace = docGraph.createSpan(docTokens.get(7));
		docGraph.addNode(theMostBeautifulPlace, docTokens.get(8), SALT_TYPE.SSPANNING_RELATION);
		docGraph.addNode(theMostBeautifulPlace, docTokens.get(9), SALT_TYPE.SSPANNING_RELATION);
		docGraph.addNode(theMostBeautifulPlace, docTokens.get(10), SALT_TYPE.SSPANNING_RELATION);		
		theMostBeautifulPlace.createAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_TYPE, "noun");		
		reference.setSource(theMostBeautifulPlace);
		reference.setTarget(newYork);
		reference.setType("reference");
		reference.createAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_REL, "non-anaphoric");
		docRefLayer.addNode(theMostBeautifulPlace);
		docRefLayer.addRelation(reference);
		///"it"
		SSpan it = docGraph.createSpan(docTokens.get(5));
		it.createAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_TYPE, "pronoun");
		reference = SaltFactory.createSPointingRelation();
		reference.setType("reference");
		reference.setSource(it);
		reference.setTarget(newYork);
		reference.createAnnotation(TCFMapperImport.LAYER_REFERENCES, TCFDictionary.ATT_REL, "anaphoric");
		docRefLayer.addNode(it);
		docRefLayer.addRelation(reference);
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_REFERENCES);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, false);
		
		/* start mapper */
				
		getFixture().mapSDocument();
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = getFixture().getDocument().getDocumentGraph();
		Set<Difference> diffs= docGraph.findDiffs(fixGraph);
		assertEquals(diffs.toString(), 0, diffs.size());
	}
	
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
		SDocument doc = SaltFactory.createSDocument();
		doc.setId("doc");
		SDocumentGraph docGraph = SaltFactory.createSDocumentGraph();
		doc.setDocumentGraph(docGraph);
		docGraph.createTextualDS(EXAMPLE_TEXT_SHRINK);
		docGraph.tokenize();
		List<SToken> docTokens = docGraph.getTokens();
		SLayer docPhoLayer = SaltFactory.createSLayer();
		docPhoLayer.setName(TCFMapperImport.LAYER_PHONETICS);
		docGraph.addLayer(docPhoLayer);
		
		docTokens.get(0).createAnnotation(TCFMapperImport.LAYER_PHONETICS, TCFDictionary.TAG_TC_PRON, "ʔa͜ɪ");
		docTokens.get(1).createAnnotation(TCFMapperImport.LAYER_PHONETICS, TCFDictionary.TAG_TC_PRON, "lʌv");
		docTokens.get(2).createAnnotation(TCFMapperImport.LAYER_PHONETICS, TCFDictionary.TAG_TC_PRON, "nu");
		docTokens.get(3).createAnnotation(TCFMapperImport.LAYER_PHONETICS, TCFDictionary.TAG_TC_PRON, "jɔɹk");
		docPhoLayer.addNode(docTokens.get(0));
		docPhoLayer.addNode(docTokens.get(1));
		docPhoLayer.addNode(docTokens.get(2));
		docPhoLayer.addNode(docTokens.get(3));
		docPhoLayer.createMetaAnnotation(null, TCFDictionary.ATT_TRANSCRIPTION, "IPA");
				
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_PHONETICS);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, true);
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, false);
		
		/* start mapper */
				
		getFixture().mapSDocument();
		
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = getFixture().getDocument().getDocumentGraph();
		Set<Difference> diffs= docGraph.findDiffs(fixGraph);
		assertEquals(diffs.toString(), 0, diffs.size());
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
		SDocument doc = SaltFactory.createSDocument();
		doc.setId("doc");
		SDocumentGraph docGraph = SaltFactory.createSDocumentGraph();
		doc.setDocumentGraph(docGraph);
		docGraph.createTextualDS(EXAMPLE_TEXT_SHRINK);
		docGraph.tokenize();
		List<SToken> docTokens = docGraph.getTokens();
		SLayer docPhoLayer = SaltFactory.createSLayer();
		docPhoLayer.setName(TCFMapperImport.LAYER_PHONETICS);
		docGraph.addLayer(docPhoLayer);
		
		SSpan sSpan = docGraph.createSpan(docTokens.get(0));
		docPhoLayer.addNode(sSpan);
		sSpan.createAnnotation(TCFMapperImport.LAYER_PHONETICS, TCFDictionary.TAG_TC_PRON, "ʔa͜ɪ");
		sSpan = docGraph.createSpan(docTokens.get(1));
		docPhoLayer.addNode(sSpan);
		sSpan.createAnnotation(TCFMapperImport.LAYER_PHONETICS, TCFDictionary.TAG_TC_PRON, "lʌv");
		sSpan = docGraph.createSpan(docTokens.get(2));
		docPhoLayer.addNode(sSpan);
		sSpan.createAnnotation(TCFMapperImport.LAYER_PHONETICS, TCFDictionary.TAG_TC_PRON, "nu");
		sSpan = docGraph.createSpan(docTokens.get(3));
		docPhoLayer.addNode(sSpan);
		sSpan.createAnnotation(TCFMapperImport.LAYER_PHONETICS, TCFDictionary.TAG_TC_PRON, "jɔɹk");
		docPhoLayer.createMetaAnnotation(null, TCFDictionary.ATT_TRANSCRIPTION, "IPA");
				
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_PHONETICS);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, false);
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, false);
		
		/* start mapper */
				
		getFixture().mapSDocument();
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = getFixture().getDocument().getDocumentGraph();
		Set<Difference> diffs= docGraph.findDiffs(fixGraph);
		assertEquals(diffs.toString(), 0, diffs.size());
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
		SDocument doc = SaltFactory.createSDocument();
		doc.setId("doc");
		SDocumentGraph docGraph = SaltFactory.createSDocumentGraph();
		doc.setDocumentGraph(docGraph);
		docGraph.createTextualDS(EXAMPLE_TEXT_ORTHOGRAPHY);
		docGraph.tokenize();
		List<SToken> docTokens = docGraph.getTokens();
		SLayer orthLayer = SaltFactory.createSLayer();
		orthLayer.setName(TCFMapperImport.LAYER_ORTHOGRAPHY);
		docGraph.addLayer(orthLayer);
		
		SAnnotation anno = null;
		SAnnotation operation = null;
		
		anno = docTokens.get(0).createAnnotation(TCFMapperImport.LAYER_ORTHOGRAPHY, TCFDictionary.TAG_TC_CORRECTION, "I");
		operation = SaltFactory.createSAnnotation();
		operation.setNamespace(TCFMapperImport.LAYER_ORTHOGRAPHY);
		operation.setName(TCFDictionary.ATT_OPERATION);
		operation.setValue("replace");
		anno.addLabel(operation);
		orthLayer.addNode(docTokens.get(0));
		
		anno = docTokens.get(1).createAnnotation(TCFMapperImport.LAYER_ORTHOGRAPHY, TCFDictionary.TAG_TC_CORRECTION, "love");
		operation = SaltFactory.createSAnnotation();
		operation.setNamespace(TCFMapperImport.LAYER_ORTHOGRAPHY);
		operation.setName(TCFDictionary.ATT_OPERATION);
		operation.setValue("replace");
		anno.addLabel(operation);
		orthLayer.addNode(docTokens.get(1));
		
		SSpan newYork = docGraph.createSpan(docTokens.get(2));
		docGraph.addNode(newYork, docTokens.get(3), SALT_TYPE.SSPANNING_RELATION);
		anno = newYork.createAnnotation(TCFMapperImport.LAYER_ORTHOGRAPHY, TCFDictionary.TAG_TC_CORRECTION, "New York");		
		operation = SaltFactory.createSAnnotation();
		operation.setNamespace(TCFMapperImport.LAYER_ORTHOGRAPHY);
		operation.setName(TCFDictionary.ATT_OPERATION);
		operation.setValue("replace");
		anno.addLabel(operation);
		orthLayer.addNode(newYork);
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_ORTHOGRAPHY);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, true);
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, false);
		
		/* start mapper */
				
		getFixture().mapSDocument();
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = getFixture().getDocument().getDocumentGraph();
		Set<Difference> diffs= docGraph.findDiffs(fixGraph);
		assertEquals(diffs.toString(), 0, diffs.size());
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
		SDocument doc = SaltFactory.createSDocument();
		doc.setId("doc");
		SDocumentGraph docGraph = SaltFactory.createSDocumentGraph();
		doc.setDocumentGraph(docGraph);
		docGraph.createTextualDS(EXAMPLE_TEXT_ORTHOGRAPHY);
		docGraph.tokenize();
		List<SToken> docTokens = docGraph.getTokens();
		SLayer orthLayer = SaltFactory.createSLayer();
		orthLayer.setName(TCFMapperImport.LAYER_ORTHOGRAPHY);
		docGraph.addLayer(orthLayer);
		Set<SNode> docOrthNodes = orthLayer.getNodes();
		
		SAnnotation anno = null;
		SAnnotation operation = null;
		SSpan sSpan = null;
		
		sSpan = docGraph.createSpan(docTokens.get(0));
		anno = sSpan.createAnnotation(TCFMapperImport.LAYER_ORTHOGRAPHY, TCFDictionary.TAG_TC_CORRECTION, "I");
		operation = SaltFactory.createSAnnotation();
		operation.setNamespace(TCFMapperImport.LAYER_ORTHOGRAPHY);
		operation.setName(TCFDictionary.ATT_OPERATION);
		operation.setValue("replace");
		anno.addLabel(operation);
		orthLayer.addNode(sSpan);
		
		sSpan = docGraph.createSpan(docTokens.get(1));
		anno = sSpan.createAnnotation(TCFMapperImport.LAYER_ORTHOGRAPHY, TCFDictionary.TAG_TC_CORRECTION, "love");
		operation = SaltFactory.createSAnnotation();
		operation.setNamespace(TCFMapperImport.LAYER_ORTHOGRAPHY);
		operation.setName(TCFDictionary.ATT_OPERATION);
		operation.setValue("replace");
		anno.addLabel(operation);
		orthLayer.addNode(sSpan);
		
		sSpan = docGraph.createSpan(docTokens.get(2));
		docGraph.addNode(sSpan, docTokens.get(3), SALT_TYPE.SSPANNING_RELATION);
		anno = sSpan.createAnnotation(TCFMapperImport.LAYER_ORTHOGRAPHY, TCFDictionary.TAG_TC_CORRECTION, "New York");		
		operation = SaltFactory.createSAnnotation();
		operation.setNamespace(TCFMapperImport.LAYER_ORTHOGRAPHY);
		operation.setName(TCFDictionary.ATT_OPERATION);
		operation.setValue("replace");
		anno.addLabel(operation);
		orthLayer.addNode(sSpan);
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_ORTHOGRAPHY);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, false);
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, false);
		
		/* start mapper */
				
		getFixture().mapSDocument();
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = getFixture().getDocument().getDocumentGraph();
		Set<Difference> diffs= docGraph.findDiffs(fixGraph);
		assertEquals(diffs.toString(), 0, diffs.size());
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
		SDocument doc = SaltFactory.createSDocument();
		doc.setId("doc");
		SDocumentGraph docGraph = SaltFactory.createSDocumentGraph();
		doc.setDocumentGraph(docGraph);
		docGraph.createTextualDS(EXAMPLE_TEXT_GEO);
		docGraph.tokenize();
		List<SToken> docTokens = docGraph.getTokens();
		SLayer docGeoLayer = SaltFactory.createSLayer();
		docGeoLayer.setName(TCFMapperImport.LAYER_GEO);
		docGraph.addLayer(docGeoLayer);
				
		SSpan newYork = docGraph.createSpan(docTokens.get(0));
		docGraph.addNode(newYork, docTokens.get(1), SALT_TYPE.SSPANNING_RELATION);
		newYork.createAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_ALT, "1");
		newYork.createAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_LAT, "2");
		newYork.createAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_LON, "3");
		newYork.createAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_CONTINENT, "North America");
		newYork.createAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_COUNTRY, "U.S.A.");
		newYork.createAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_CAPITAL, "Washington (D.C.)");
		docGeoLayer.addNode(newYork);

		docTokens.get(4).createAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_ALT, "2");
		docTokens.get(4).createAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_LAT, "3");
		docTokens.get(4).createAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_LON, "1");
		docTokens.get(4).createAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_CONTINENT, "Europe");
		docTokens.get(4).createAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_COUNTRY, "Germany");
		docTokens.get(4).createAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_CAPITAL, "Berlin");
		docGeoLayer.addNode(docTokens.get(4));
		
		docGeoLayer.createMetaAnnotation(null, TCFDictionary.ATT_COORDFORMAT, "DegDec");
		docGeoLayer.createMetaAnnotation(null, TCFDictionary.ATT_CONTINENTFORMAT, "name");
		docGeoLayer.createMetaAnnotation(null, TCFDictionary.ATT_COUNTRYFORMAT, "ISO3166_A2");
		docGeoLayer.createMetaAnnotation(null, TCFDictionary.ATT_CAPITALFORMAT, "name");
		docGeoLayer.createMetaAnnotation(null, TCFDictionary.TAG_TC_SRC, "my fantasy");
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_GEO);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, true);
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, false);
		
		/* start mapper */
				
		getFixture().mapSDocument();
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = getFixture().getDocument().getDocumentGraph();
		Set<Difference> diffs= docGraph.findDiffs(fixGraph);
		assertEquals(diffs.toString(), 0, diffs.size());
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
		SDocument doc = SaltFactory.createSDocument();
		doc.setId("doc");
		SDocumentGraph docGraph = SaltFactory.createSDocumentGraph();
		doc.setDocumentGraph(docGraph);
		docGraph.createTextualDS(EXAMPLE_TEXT_GEO);
		docGraph.tokenize();
		List<SToken> docTokens = docGraph.getTokens();
		SLayer docGeoLayer = SaltFactory.createSLayer();
		docGeoLayer.setName(TCFMapperImport.LAYER_GEO);
		docGraph.addLayer(docGeoLayer);
				
		SSpan newYork = docGraph.createSpan(docTokens.get(0));
		docGraph.addNode(newYork, docTokens.get(1), SALT_TYPE.SSPANNING_RELATION);
		newYork.createAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_ALT, "1");
		newYork.createAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_LAT, "2");
		newYork.createAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_LON, "3");
		newYork.createAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_CONTINENT, "North America");
		newYork.createAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_COUNTRY, "U.S.A.");
		newYork.createAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_CAPITAL, "Washington (D.C.)");
		docGeoLayer.addNode(newYork);

		SSpan berlin = docGraph.createSpan(docTokens.get(4));
		berlin.createAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_ALT, "2");
		berlin.createAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_LAT, "3");
		berlin.createAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_LON, "1");
		berlin.createAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_CONTINENT, "Europe");
		berlin.createAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_COUNTRY, "Germany");
		berlin.createAnnotation(TCFMapperImport.LAYER_GEO, TCFDictionary.ATT_CAPITAL, "Berlin");
		docGeoLayer.addNode(berlin);
		
		docGeoLayer.createMetaAnnotation(null, TCFDictionary.ATT_COORDFORMAT, "DegDec");
		docGeoLayer.createMetaAnnotation(null, TCFDictionary.ATT_CONTINENTFORMAT, "name");
		docGeoLayer.createMetaAnnotation(null, TCFDictionary.ATT_COUNTRYFORMAT, "ISO3166_A2");
		docGeoLayer.createMetaAnnotation(null, TCFDictionary.ATT_CAPITALFORMAT, "name");
		docGeoLayer.createMetaAnnotation(null, TCFDictionary.TAG_TC_SRC, "my fantasy");
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_GEO);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, false);
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, false);
		
		/* start mapper */
				
		getFixture().mapSDocument();
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = getFixture().getDocument().getDocumentGraph();
		Set<Difference> diffs= docGraph.findDiffs(fixGraph, (new DiffOptions()).setOption(DiffOptions.OPTION_IGNORE_ID, true));
		assertEquals(diffs.toString(), 0, diffs.size());
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
		SDocument doc = SaltFactory.createSDocument();
		SDocumentGraph docGraph = SaltFactory.createSDocumentGraph();		
		doc.setDocumentGraph(docGraph);
		docGraph.createTextualDS(EXAMPLE_TEXT_SHRINK);
		docGraph.tokenize();
		List<SToken> docTokens = docGraph.getTokens();
		SLayer docLexLayer = SaltFactory.createSLayer();
		SLayer docLemmaLayer = SaltFactory.createSLayer();
		docLexLayer.setName(TCFMapperImport.LAYER_LS);
		docLemmaLayer.setName(TCFMapperImport.LAYER_LEMMA);
		docGraph.addLayer(docLexLayer);
		docGraph.addLayer(docLemmaLayer);

		SAnnotation synonym = SaltFactory.createSAnnotation();
		synonym.setNamespace(TCFMapperImport.LAYER_LS);
		synonym.setName(TCFDictionary.TAG_TC_SYNONYMY);	
		
		SAnnotation antonym = SaltFactory.createSAnnotation();
		antonym.setNamespace(TCFMapperImport.LAYER_LS);
		antonym.setName(TCFDictionary.TAG_TC_ANTONYMY);
		
		SAnnotation hyponym = SaltFactory.createSAnnotation();
		hyponym.setNamespace(TCFMapperImport.LAYER_LS);
		hyponym.setName(TCFDictionary.TAG_TC_HYPONYMY);
		
		SAnnotation hyperonym = SaltFactory.createSAnnotation();
		hyperonym.setNamespace(TCFMapperImport.LAYER_LS);
		hyperonym.setName(TCFDictionary.TAG_TC_HYPERONYMY);
		
		SAnnotation docLemma = SaltFactory.createSLemmaAnnotation();
		docLemma.setValue("I");
		docTokens.get(0).addAnnotation(docLemma);
		antonym.setValue("the set of human/animate entities in this world not including me");
		hyponym.setValue("you, he, she, it, we, you, they");
		hyperonym.setValue("PPERs");
		docLemma.addLabel(antonym);
		docLemma.addLabel(hyponym);
		docLemma.addLabel(hyperonym);
		docLemmaLayer.addNode(docTokens.get(0));
		
		docLemma = SaltFactory.createSLemmaAnnotation();
		docLemma.setValue("love");
		docTokens.get(1).addAnnotation(docLemma);
		antonym = (SAnnotation)antonym.copy(SaltFactory.createSAnnotation());
		hyponym = (SAnnotation)hyponym.copy(SaltFactory.createSAnnotation());
		hyperonym = (SAnnotation)hyperonym.copy(SaltFactory.createSAnnotation());
		synonym.setValue("admire, like");		
		antonym.setValue("hate, dislike");
		hyponym.setValue("hate, dislike, fear, appreciate, ...");
		hyperonym.setValue("verbs of experience");
		docLemma.addLabel(synonym);
		docLemma.addLabel(antonym);
		docLemma.addLabel(hyponym);
		docLemma.addLabel(hyperonym);
		docLemmaLayer.addNode(docTokens.get(1));
		
		docLemma = SaltFactory.createSLemmaAnnotation();
		docLemma.setValue("New York"); 
		SSpan newYork = docGraph.createSpan(docTokens.get(2));
		docGraph.addNode(newYork, docTokens.get(3), SALT_TYPE.SSPANNING_RELATION);
		newYork.addAnnotation(docLemma);
		synonym = (SAnnotation)synonym.copy(SaltFactory.createSAnnotation());
		antonym = (SAnnotation)antonym.copy(SaltFactory.createSAnnotation());
		hyponym = (SAnnotation)hyponym.copy(SaltFactory.createSAnnotation());
		hyperonym = (SAnnotation)hyperonym.copy(SaltFactory.createSAnnotation());
		synonym.setValue("N.Y., Big Apple");		
		antonym.setValue("the set of places not including New York");
		hyponym.setValue("Schweinfurth, Graz, Cannes, Manchester, ...");
		hyperonym.setValue("cities");
		docLemma.addLabel(synonym);
		docLemma.addLabel(antonym);
		docLemma.addLabel(hyponym);
		docLemma.addLabel(hyperonym);
		docLemmaLayer.addNode(newYork);
		
		docLemma = SaltFactory.createSLemmaAnnotation();
		docLemma.setValue(".");
		docTokens.get(4).addAnnotation(docLemma);
		hyponym = (SAnnotation)hyponym.copy(SaltFactory.createSAnnotation());
		hyperonym = (SAnnotation)hyperonym.copy(SaltFactory.createSAnnotation());
		hyponym.setValue("!, ?");
		hyperonym.setValue("sentence final characters");
		docLemma.addLabel(hyponym);
		docLemma.addLabel(hyperonym);
		docLemmaLayer.addNode(docTokens.get(4));
				
		/* since the parser first hits synonymy and token 0 and 4 (1 and 5) are not contained, we must add them in order of their appearance */
		docLexLayer.addNode(docTokens.get(1));
		docLexLayer.addNode(newYork);
		docLexLayer.addNode(docTokens.get(0));
		docLexLayer.addNode(docTokens.get(4));
				
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_LEXICALSEMANTICS);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, true);
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, false);
		
		/* start mapper */
				
		getFixture().mapSDocument();
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = getFixture().getDocument().getDocumentGraph();
		Set<Difference> diffs= docGraph.findDiffs(fixGraph);
		assertEquals(diffs.toString(), 0, diffs.size());
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
		SDocument doc = SaltFactory.createSDocument();
		doc.setId("doc");
		SDocumentGraph docGraph = SaltFactory.createSDocumentGraph();
		doc.setDocumentGraph(docGraph);
		docGraph.createTextualDS(EXAMPLE_TEXT_SHRINK);
		docGraph.tokenize();
		List<SToken> docTokens = docGraph.getTokens();
		SLayer docLexLayer = SaltFactory.createSLayer();
		docLexLayer.setName(TCFMapperImport.LAYER_LS);
		SLayer docLemmaLayer = SaltFactory.createSLayer();
		docLemmaLayer.setName(TCFMapperImport.LAYER_LEMMA);
		docGraph.addLayer(docLexLayer);
		docGraph.addLayer(docLemmaLayer);

		SAnnotation synonym = SaltFactory.createSAnnotation();
		synonym.setNamespace(TCFMapperImport.LAYER_LS);
		synonym.setName(TCFDictionary.TAG_TC_SYNONYMY);	
		
		SAnnotation antonym = SaltFactory.createSAnnotation();
		antonym.setNamespace(TCFMapperImport.LAYER_LS);
		antonym.setName(TCFDictionary.TAG_TC_ANTONYMY);
		
		SAnnotation hyponym = SaltFactory.createSAnnotation();
		hyponym.setNamespace(TCFMapperImport.LAYER_LS);
		hyponym.setName(TCFDictionary.TAG_TC_HYPONYMY);
		
		SAnnotation hyperonym = SaltFactory.createSAnnotation();
		hyperonym.setNamespace(TCFMapperImport.LAYER_LS);
		hyperonym.setName(TCFDictionary.TAG_TC_HYPERONYMY);
		
		SAnnotation docLemma = SaltFactory.createSLemmaAnnotation();
		docLemma.setValue("I");
		SSpan span0 = docGraph.createSpan(docTokens.get(0));
		span0.addAnnotation(docLemma);
		antonym.setValue("the set of human/animate entities in this world not including me");
		hyponym.setValue("you, he, she, it, we, you, they");
		hyperonym.setValue("PPERs");
		docLemma.addLabel(antonym);
		docLemma.addLabel(hyponym);
		docLemma.addLabel(hyperonym);
		docLemmaLayer.addNode(span0);
		
		docLemma = SaltFactory.createSLemmaAnnotation();
		docLemma.setValue("love");
		SSpan span1 = docGraph.createSpan(docTokens.get(1));
		span1.addAnnotation(docLemma);
		antonym = (SAnnotation)antonym.copy(SaltFactory.createSAnnotation());
		hyponym = (SAnnotation)hyponym.copy(SaltFactory.createSAnnotation());
		hyperonym = (SAnnotation)hyperonym.copy(SaltFactory.createSAnnotation());
		synonym.setValue("admire, like");		
		antonym.setValue("hate, dislike");
		hyponym.setValue("hate, dislike, fear, appreciate, ...");
		hyperonym.setValue("verbs of experience");
		docLemma.addLabel(synonym);
		docLemma.addLabel(antonym);
		docLemma.addLabel(hyponym);
		docLemma.addLabel(hyperonym);
		docLemmaLayer.addNode(span1);
		
		docLemma = SaltFactory.createSLemmaAnnotation();
		docLemma.setValue("New York"); 
		SSpan newYork = docGraph.createSpan(docTokens.get(2));
		docGraph.addNode(newYork, docTokens.get(3), SALT_TYPE.SSPANNING_RELATION);
		newYork.addAnnotation(docLemma);
		synonym = (SAnnotation)synonym.copy(SaltFactory.createSAnnotation());
		antonym = (SAnnotation)antonym.copy(SaltFactory.createSAnnotation());
		hyponym = (SAnnotation)hyponym.copy(SaltFactory.createSAnnotation());
		hyperonym = (SAnnotation)hyperonym.copy(SaltFactory.createSAnnotation());
		synonym.setValue("N.Y., Big Apple");		
		antonym.setValue("the set of places not including New York");
		hyponym.setValue("Schweinfurth, Graz, Cannes, Manchester, ...");
		hyperonym.setValue("cities");
		docLemma.addLabel(synonym);
		docLemma.addLabel(antonym);
		docLemma.addLabel(hyponym);
		docLemma.addLabel(hyperonym);
		docLemmaLayer.addNode(newYork);
		
		docLemma = SaltFactory.createSLemmaAnnotation();
		docLemma.setValue(".");
		SSpan span3 = docGraph.createSpan(docTokens.get(4));
		span3.addAnnotation(docLemma);
		hyponym = (SAnnotation)hyponym.copy(SaltFactory.createSAnnotation());
		hyperonym = (SAnnotation)hyperonym.copy(SaltFactory.createSAnnotation());
		hyponym.setValue("!, ?");
		hyperonym.setValue("sentence final characters");
		docLemma.addLabel(hyponym);
		docLemma.addLabel(hyperonym);
		docLemmaLayer.addNode(span3);
				
		/* since the parser first hits synonymy and token 0 and 4 (1 and 5) are not contained, we must add them in order of their appearance */
		docLexLayer.addNode(docGraph.getSpans().get(1));
		docLexLayer.addNode(newYork);
		docLexLayer.addNode(docGraph.getSpans().get(0));
		docLexLayer.addNode(docGraph.getSpans().get(3));
				
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_LEXICALSEMANTICS);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, false);
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, false);
		
		/* start mapper */
				
		getFixture().mapSDocument();
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = getFixture().getDocument().getDocumentGraph();
		Set<Difference> diffs= docGraph.findDiffs(fixGraph);
		assertEquals(diffs.toString(), 0, diffs.size());
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
		SDocument doc = SaltFactory.createSDocument();
		doc.setId("doc");
		SDocumentGraph docGraph = SaltFactory.createSDocumentGraph();
		doc.setDocumentGraph(docGraph);
		docGraph.createTextualDS(EXAMPLE_TEXT_SHRINK);
		docGraph.tokenize();
		List<SToken> docTokens = docGraph.getTokens();
		SLayer docWSLayer = SaltFactory.createSLayer();
		docWSLayer.setName(TCFMapperImport.LAYER_WORDSENSE);
		docWSLayer.createMetaAnnotation(null, TCFDictionary.ATT_SRC, "any source");
		docGraph.addLayer(docWSLayer);
		
		docTokens.get(1).createAnnotation(TCFMapperImport.LAYER_WORDSENSE, TCFDictionary.ATT_LEXUNITS, "14");
		docTokens.get(1).createAnnotation(TCFMapperImport.LAYER_WORDSENSE, TCFDictionary.ATT_COMMENT, "C.E.");
		docWSLayer.addNode(docTokens.get(1));
		
		SSpan newYork = docGraph.createSpan(docTokens.get(2));
		docGraph.addNode(newYork, docTokens.get(3), SALT_TYPE.SSPANNING_RELATION);
		newYork.createAnnotation(TCFMapperImport.LAYER_WORDSENSE, TCFDictionary.ATT_LEXUNITS, "1 2 3");
		newYork.createAnnotation(TCFMapperImport.LAYER_WORDSENSE, TCFDictionary.ATT_COMMENT, "from there to Germany");
		docWSLayer.addNode(newYork);		
		
		docTokens.get(4).createAnnotation(TCFMapperImport.LAYER_WORDSENSE , TCFDictionary.ATT_LEXUNITS, "0");
		docWSLayer.addNode(docTokens.get(4));
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_LEXICALSEMANTICS);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, true);
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, false);
		
		/* start mapper */
				
		getFixture().mapSDocument();
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = getFixture().getDocument().getDocumentGraph();
		Set<Difference> diffs= docGraph.findDiffs(fixGraph);
		assertEquals(diffs.toString(), 0, diffs.size());
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
		SDocument doc = SaltFactory.createSDocument();
		doc.setId("doc");
		SDocumentGraph docGraph = SaltFactory.createSDocumentGraph();
		doc.setDocumentGraph(docGraph);
		docGraph.createTextualDS(EXAMPLE_TEXT_SHRINK);
		docGraph.tokenize();
		List<SToken> docTokens = docGraph.getTokens();
		SLayer docWSLayer = SaltFactory.createSLayer();
		docWSLayer.setName(TCFMapperImport.LAYER_WORDSENSE);
		docWSLayer.createMetaAnnotation(null, TCFDictionary.ATT_SRC, "any source");
		docGraph.addLayer(docWSLayer);
		
		SSpan sSpan = docGraph.createSpan(docTokens.get(1));
		sSpan.createAnnotation(TCFMapperImport.LAYER_WORDSENSE, TCFDictionary.ATT_LEXUNITS, "14");
		sSpan.createAnnotation(TCFMapperImport.LAYER_WORDSENSE, TCFDictionary.ATT_COMMENT, "C.E.");
		docWSLayer.addNode(sSpan);
		
		sSpan = docGraph.createSpan(docTokens.get(2));
		docGraph.addNode(sSpan, docTokens.get(3), SALT_TYPE.SSPANNING_RELATION);
		sSpan.createAnnotation(TCFMapperImport.LAYER_WORDSENSE, TCFDictionary.ATT_LEXUNITS, "1 2 3");
		sSpan.createAnnotation(TCFMapperImport.LAYER_WORDSENSE, TCFDictionary.ATT_COMMENT, "from there to Germany");
		docWSLayer.addNode(sSpan);		
		
		sSpan = docGraph.createSpan(docTokens.get(4));
		sSpan.createAnnotation(TCFMapperImport.LAYER_WORDSENSE , TCFDictionary.ATT_LEXUNITS, "0");
		docWSLayer.addNode(sSpan);
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_LEXICALSEMANTICS);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, false);
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, false);
		
		/* start mapper */
				
		getFixture().mapSDocument();
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = getFixture().getDocument().getDocumentGraph();
		Set<Difference> diffs= docGraph.findDiffs(fixGraph);
		assertEquals(diffs.toString(), 0, diffs.size());
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
		SDocument doc = SaltFactory.createSDocument();
		doc.setId("doc");
		doc.setDocumentGraph(SaltFactory.createSDocumentGraph());
		SampleGenerator.createPrimaryData(doc);
		SampleGenerator.createTokens(doc);		
		SDocumentGraph docGraph = doc.getDocumentGraph();
		docGraph.removeLayer(docGraph.getLayers().iterator().next());
		List<SToken> docTokens = docGraph.getTokens();
		SLayer docSplitLayer = SaltFactory.createSLayer();
		docSplitLayer.setName(TCFMapperImport.LAYER_SPLITTINGS);
		docGraph.addLayer(docSplitLayer);
		
		docSplitLayer.createMetaAnnotation(null, TCFDictionary.ATT_TYPE, "syllables");
		//example:
		docTokens.get(1).createAnnotation(TCFMapperImport.LAYER_SPLITTINGS, TCFDictionary.TAG_TC_SPLIT, "1"); //I know ...
		docSplitLayer.addNode(docTokens.get(1));
		
		//complicated:
		docTokens.get(4).createAnnotation(TCFMapperImport.LAYER_SPLITTINGS, TCFDictionary.TAG_TC_SPLIT, "3 6 8");
		docSplitLayer.addNode(docTokens.get(4));
		
		//appears:
		docTokens.get(7).createAnnotation(TCFMapperImport.LAYER_SPLITTINGS, TCFDictionary.TAG_TC_SPLIT, "2");
		docSplitLayer.addNode(docTokens.get(7));
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_LEXICALSEMANTICS);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, true);
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, false);
		
		/* start mapper */
				
		getFixture().mapSDocument();
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = getFixture().getDocument().getDocumentGraph();
		Set<Difference> diffs= docGraph.findDiffs(fixGraph);
		assertEquals(diffs.toString(), 0, diffs.size());
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
		SDocument doc = SaltFactory.createSDocument();
		doc.setId("doc");
		doc.setDocumentGraph(SaltFactory.createSDocumentGraph());
		SampleGenerator.createPrimaryData(doc);
		SampleGenerator.createTokens(doc);
		SDocumentGraph docGraph = doc.getDocumentGraph();
		docGraph.removeLayer(docGraph.getLayers().iterator().next());
		List<SToken> docTokens = docGraph.getTokens();
		SLayer docSplitLayer = SaltFactory.createSLayer();
		docSplitLayer.setName(TCFMapperImport.LAYER_SPLITTINGS);
		docGraph.addLayer(docSplitLayer);
		docSplitLayer.createMetaAnnotation(null, TCFDictionary.ATT_TYPE, "syllables");
		//example:
		SSpan sSpan = docGraph.createSpan(docTokens.get(1));
		sSpan.createAnnotation(TCFMapperImport.LAYER_SPLITTINGS, TCFDictionary.TAG_TC_SPLIT, "1"); //I know ...
		docSplitLayer.addNode(sSpan);
		
		//complicated:
		sSpan = docGraph.createSpan(docTokens.get(4));
		sSpan.createAnnotation(TCFMapperImport.LAYER_SPLITTINGS, TCFDictionary.TAG_TC_SPLIT, "3 6 8"); //I know ...
		docSplitLayer.addNode(sSpan);
		
		//appears:
		sSpan = docGraph.createSpan(docTokens.get(7));
		sSpan.createAnnotation(TCFMapperImport.LAYER_SPLITTINGS, TCFDictionary.TAG_TC_SPLIT, "2"); //I know ...
		docSplitLayer.addNode(sSpan);
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_LEXICALSEMANTICS);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, false);
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, false);
		
		/* start mapper */
				
		getFixture().mapSDocument();
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = getFixture().getDocument().getDocumentGraph();
		Set<Difference> diffs= docGraph.findDiffs(fixGraph);
		assertEquals(diffs.toString(), 0, diffs.size());
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
		SDocument doc = SaltFactory.createSDocument();
		doc.setId("doc");
		SDocumentGraph docGraph = SaltFactory.createSDocumentGraph();
		doc.setDocumentGraph(docGraph);
		docGraph.createTextualDS(EXAMPLE_TEXT_DISCOURSE_CONNECTIVES);
		docGraph.tokenize();
		List<SToken> docTokens = docGraph.getTokens();
		SLayer docDiscourseLayer = SaltFactory.createSLayer();
		docDiscourseLayer.setName(TCFMapperImport.LAYER_DISCOURSE);
		docGraph.addLayer(docDiscourseLayer);
		
		docDiscourseLayer.createMetaAnnotation(null, TCFDictionary.ATT_TAGSET, "any tagset");
		
		//since:
		docTokens.get(0).createAnnotation(TCFMapperImport.LAYER_DISCOURSE, TCFDictionary.ATT_TYPE, "temporal");
		docDiscourseLayer.addNode(docTokens.get(0));
		//than:
		docTokens.get(7).createAnnotation(TCFMapperImport.LAYER_DISCOURSE, TCFDictionary.ATT_TYPE, "comparative");
		docDiscourseLayer.addNode(docTokens.get(7));
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_DISCOURSE_CONNECTIVES);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, true);
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, false);
		
		/* start mapper */
				
		getFixture().mapSDocument();
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = getFixture().getDocument().getDocumentGraph();
		Set<Difference> diffs= docGraph.findDiffs(fixGraph);
		assertEquals(diffs.toString(), 0, diffs.size());
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
		SDocument doc = SaltFactory.createSDocument();
		doc.setId("doc");
		SDocumentGraph docGraph = SaltFactory.createSDocumentGraph();
		doc.setDocumentGraph(docGraph);
		docGraph.createTextualDS(EXAMPLE_TEXT_DISCOURSE_CONNECTIVES);
		docGraph.tokenize();
		List<SToken> docTokens = docGraph.getTokens();
		SLayer docDiscourseLayer = SaltFactory.createSLayer();
		docDiscourseLayer.setName(TCFMapperImport.LAYER_DISCOURSE);
		docGraph.addLayer(docDiscourseLayer);
		Set<SNode> docDiscourseNodes = docDiscourseLayer.getNodes();
		
		docDiscourseLayer.createMetaAnnotation(null, TCFDictionary.ATT_TAGSET, "any tagset");
		
		//since:
		SSpan sSpan = docGraph.createSpan(docTokens.get(0));
		sSpan.createAnnotation(TCFMapperImport.LAYER_DISCOURSE, TCFDictionary.ATT_TYPE, "temporal");
		docDiscourseLayer.addNode(sSpan);
		//than:
		sSpan = docGraph.createSpan(docTokens.get(7));
		sSpan.createAnnotation(TCFMapperImport.LAYER_DISCOURSE, TCFDictionary.ATT_TYPE, "comparative");
		docDiscourseLayer.addNode(sSpan);
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_DISCOURSE_CONNECTIVES);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, false);
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, false);
		
		/* start mapper */
				
		getFixture().mapSDocument();
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = getFixture().getDocument().getDocumentGraph();
		Set<Difference> diffs= docGraph.findDiffs(fixGraph);
		assertEquals(diffs.toString(), 0, diffs.size());
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
		SDocument doc = SaltFactory.createSDocument();
		doc.setId("doc0");
		SDocumentGraph docGraph = SaltFactory.createSDocumentGraph();
		doc.setDocumentGraph(docGraph);
		docGraph.createTextualDS(EXAMPLE_TEXT);
		docGraph.tokenize();
		List<SToken> docTokens = docGraph.getTokens();
		SLayer docTextLayer = SaltFactory.createSLayer();
		docTextLayer.setName(TCFMapperImport.LAYER_TEXTSTRUCTURE);
		docGraph.addLayer(docTextLayer);
		
		//paragraph:
		SSpan sSpan = docGraph.createSpan(docTokens);
		sSpan.createAnnotation(TCFMapperImport.LAYER_TEXTSTRUCTURE, TCFDictionary.ATT_TYPE, "paragraph");
		docTextLayer.addNode(sSpan);
		
		//page1:
		List<SToken> spanTokens = new ArrayList<SToken>();
		spanTokens.add(docTokens.get(0));
		spanTokens.add(docTokens.get(1));
		spanTokens.add(docTokens.get(2));
		spanTokens.add(docTokens.get(3));
		spanTokens.add(docTokens.get(4));
		sSpan = docGraph.createSpan(spanTokens);
		sSpan.createAnnotation(TCFMapperImport.LAYER_TEXTSTRUCTURE, TCFDictionary.ATT_TYPE, "page");
		docTextLayer.addNode(sSpan);
		
		//page2:
		spanTokens.clear();
		spanTokens.add(docTokens.get(5));
		spanTokens.add(docTokens.get(6));
		spanTokens.add(docTokens.get(7));
		spanTokens.add(docTokens.get(8));
		spanTokens.add(docTokens.get(9));
		spanTokens.add(docTokens.get(10));
		sSpan = docGraph.createSpan(spanTokens);
		sSpan.createAnnotation(TCFMapperImport.LAYER_TEXTSTRUCTURE, TCFDictionary.ATT_TYPE, "page");
		docTextLayer.addNode(sSpan);
		
		//line1:
		docTokens.get(0).createAnnotation(TCFMapperImport.LAYER_TEXTSTRUCTURE, TCFDictionary.ATT_TYPE, "line");
		docTextLayer.addNode(docTokens.get(0));
		
		//line2:
		spanTokens.clear();
		spanTokens.add(docTokens.get(1));
		spanTokens.add(docTokens.get(2));
		spanTokens.add(docTokens.get(3));
		spanTokens.add(docTokens.get(4));
		sSpan = docGraph.createSpan(spanTokens);
		sSpan.createAnnotation(TCFMapperImport.LAYER_TEXTSTRUCTURE, TCFDictionary.ATT_TYPE, "line");
		docTextLayer.addNode(sSpan);
		
		//line3:
		spanTokens.clear();
		spanTokens.add(docTokens.get(5));
		spanTokens.add(docTokens.get(6));
		spanTokens.add(docTokens.get(7));
		spanTokens.add(docTokens.get(8));
		spanTokens.add(docTokens.get(9));
		spanTokens.add(docTokens.get(10));
		sSpan = docGraph.createSpan(spanTokens);
		sSpan.createAnnotation(TCFMapperImport.LAYER_TEXTSTRUCTURE, TCFDictionary.ATT_TYPE, "line");
		docTextLayer.addNode(sSpan);
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_TEXTSTRUCTURE);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, true);
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, false);
		
		/* start mapper */
				
		getFixture().mapSDocument();
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = getFixture().getDocument().getDocumentGraph();System.out.println(fixGraph);
		Set<Difference> diffs= docGraph.findDiffs(fixGraph, (new DiffOptions()).setOption(DiffOptions.OPTION_IGNORE_ID, true));
		assertEquals(diffs.toString(), 0, diffs.size());
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
		SDocument doc = SaltFactory.createSDocument();
		doc.setId("doc"); //if you comment this out, the test will fail
		SDocumentGraph docGraph = SaltFactory.createSDocumentGraph();
		doc.setDocumentGraph(docGraph);
		docGraph.createTextualDS(EXAMPLE_TEXT);
		docGraph.tokenize();
		List<SToken> docTokens = docGraph.getTokens();
		SLayer docTextLayer = SaltFactory.createSLayer();
		docTextLayer.setName(TCFMapperImport.LAYER_TEXTSTRUCTURE);
		docGraph.addLayer(docTextLayer);
		
		//paragraph:
		SSpan sSpan = docGraph.createSpan(docTokens);
		sSpan.createAnnotation(TCFMapperImport.LAYER_TEXTSTRUCTURE, TCFDictionary.ATT_TYPE, "paragraph");
		sSpan.addLayer(docTextLayer);
		
		//page1:
		List<SToken> spanTokens = new ArrayList<SToken>();
		spanTokens.add(docTokens.get(0));
		spanTokens.add(docTokens.get(1));
		spanTokens.add(docTokens.get(2));
		spanTokens.add(docTokens.get(3));
		spanTokens.add(docTokens.get(4));
		sSpan = docGraph.createSpan(spanTokens);
		sSpan.createAnnotation(TCFMapperImport.LAYER_TEXTSTRUCTURE, TCFDictionary.ATT_TYPE, "page");
		sSpan.addLayer(docTextLayer);
		
		//page2:
		spanTokens.clear();
		spanTokens.add(docTokens.get(5));
		spanTokens.add(docTokens.get(6));
		spanTokens.add(docTokens.get(7));
		spanTokens.add(docTokens.get(8));
		spanTokens.add(docTokens.get(9));
		spanTokens.add(docTokens.get(10));
		sSpan = docGraph.createSpan(spanTokens);
		sSpan.createAnnotation(TCFMapperImport.LAYER_TEXTSTRUCTURE, TCFDictionary.ATT_TYPE, "page");
		sSpan.addLayer(docTextLayer);
		
		//line1:
		sSpan = docGraph.createSpan(docTokens.get(0));
		sSpan.createAnnotation(TCFMapperImport.LAYER_TEXTSTRUCTURE, TCFDictionary.ATT_TYPE, "line");
		sSpan.addLayer(docTextLayer);
		
		//line2:
		spanTokens.clear();
		spanTokens.add(docTokens.get(1));
		spanTokens.add(docTokens.get(2));
		spanTokens.add(docTokens.get(3));
		spanTokens.add(docTokens.get(4));
		sSpan = docGraph.createSpan(spanTokens);
		sSpan.createAnnotation(TCFMapperImport.LAYER_TEXTSTRUCTURE, TCFDictionary.ATT_TYPE, "line");
		sSpan.addLayer(docTextLayer);
		
		//line3:
		spanTokens.clear();
		spanTokens.add(docTokens.get(5));
		spanTokens.add(docTokens.get(6));
		spanTokens.add(docTokens.get(7));
		spanTokens.add(docTokens.get(8));
		spanTokens.add(docTokens.get(9));
		spanTokens.add(docTokens.get(10));
		sSpan = docGraph.createSpan(spanTokens);
		sSpan.createAnnotation(TCFMapperImport.LAYER_TEXTSTRUCTURE, TCFDictionary.ATT_TYPE, "line");
		sSpan.addLayer(docTextLayer);
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_TEXTSTRUCTURE);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, false);
		getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_USE_COMMON_ANNOTATED_ELEMENT, false);
		
		/* start mapper */
				
		getFixture().mapSDocument();
				
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = getFixture().getDocument().getDocumentGraph();
		
		Set<Difference> diffs= docGraph.findDiffs(fixGraph, (new DiffOptions()).setOption(DiffOptions.OPTION_IGNORE_ID, true));
		assertEquals(diffs.toString(), 0, diffs.size());		
	}	
}