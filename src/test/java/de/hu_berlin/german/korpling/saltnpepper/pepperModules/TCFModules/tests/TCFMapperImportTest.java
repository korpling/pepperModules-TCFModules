package de.hu_berlin.german.korpling.saltnpepper.pepperModules.TCFModules.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.junit.Before;
import org.junit.Test;

import de.hu_berlin.german.korpling.saltnpepper.pepperModules.tcfModules.TCFDictionary;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.tcfModules.TCFMapperImport;
import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDataSourceSequence;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SPointingRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpanningRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STYPE_NAME;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltSample.SaltSample;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltSemantics.SALT_SEMANTIC_NAMES;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltSemantics.SaltSemanticsPackage;

public class TCFMapperImportTest {

	private TCFMapperImport fixture;
	private static final String EXAMPLE_TEXT = "Is this example more complicated than it appears to be?";
	private static final String LOCATION_TEST_PRIMARY_TEXT = "/pepper-test/tcfImporterTest.xml";
	private static final String LOCATION_TEST_TOKENS = "/pepper-test/tcfImporterTestTokens.xml";
	private static final String LOCATION_TEST_TOKENS_POS = "/pepper-test/tcfImporterTestTokensPos.xml";
	private static final String LOCATION_TEST_TOKENS_LEMMA = "/pepper-test/tcfImporterTestTokensLemma.xml";
	private static final String LOCATION_TEST_SENTENCE = "/pepper-test/tcfImporterTestSentence.xml";
	
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
	}

	/**
	 * this method creates the xml-document used in the particular tests
	 * depending on the test mode
	 * @return
	 * @throws FactoryConfigurationError 
	 * @throws XMLStreamException 
	 */
	private XMLStreamWriter getTestDocument(TEST_MODE mode) throws XMLStreamException, FactoryConfigurationError{
		XMLStreamWriter xmlWriter = XMLOutputFactory.newFactory().createXMLStreamWriter(new ByteArrayOutputStream());
		
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
		
		/** TODO
		 */
		
		return(xmlWriter);
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
		
		/*start mapper*/
		System.out.println(tmpOut);		
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
		
		/* start mapper */
		System.out.println(tmpOut);		
		this.getFixture().mapSDocument();
				
		/* test from testPrimaryData*/
		SDocumentGraph fixGraph = getFixture().getSDocument().getSDocumentGraph();
		
		assertNotNull(getFixture().getSDocument());
		assertNotNull(getFixture().getSDocument().getSDocumentGraph());
		assertEquals(docGraph.getSTextualDSs().size(), fixGraph.getSTextualDSs().size());		
		assertEquals(docGraph.getSTextualDSs().get(0).getSText().length(), fixGraph.getSTextualDSs().get(0).getSText().length());
		assertEquals(docGraph.getSTextualDSs().get(0).getSText(), fixGraph.getSTextualDSs().get(0).getSText());	
		
		/* compare template salt model to imported salt model */
		
		assertNotEquals(fixGraph.getSTextualDSs().size(), 0);
		assertNotEquals(fixGraph.getSTokens().size(), 0);
		assertEquals(docGraph.getSTokens().size(), fixGraph.getSTokens().size());		
		
		for(int i=0; i<docGraph.getSTextualRelations().size(); i++){
			assertEquals(docGraph.getSTextualRelations().get(i).getSStart(), fixGraph.getSTextualRelations().get(i).getSStart());
		}
		
	}
	/**
	 * This method tests if a valid TCF-XML-structure containing pos-tagged tokens
	 * is converted to salt correctly by {@link TCFMapperImport} 
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testTokensPos() throws XMLStreamException, FileNotFoundException{
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
					xmlWriter.writeStartElement(TCFDictionary.TAG_TC_POSTAGS);
					xmlWriter.writeAttribute(TCFDictionary.ATT_TAGSET, "penn");
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_TAG);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt1");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t1");
						xmlWriter.writeCharacters("VBZ");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_TAG);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt2");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t2");
						xmlWriter.writeCharacters("DT");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_TAG);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt3");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t3");
						xmlWriter.writeCharacters("NN");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_TAG);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt4");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t4");
						xmlWriter.writeCharacters("ABR");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_TAG);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt5");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t5");
						xmlWriter.writeCharacters("JJ");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_TAG);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt6");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t6");
						xmlWriter.writeCharacters("IN");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_TAG);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt7");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t7");
						xmlWriter.writeCharacters("PRP");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_TAG);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt8");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t8");
						xmlWriter.writeCharacters("VBZ");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_TAG);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt9");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t9");
						xmlWriter.writeCharacters("TO");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_TAG);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt10");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t10");
						xmlWriter.writeCharacters("VB");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_TAG);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt11");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t11");
						xmlWriter.writeCharacters(".");
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
		SaltSample.createMorphologyAnnotations2(doc); //pos'n'stuff
		SDocumentGraph docGraph = doc.getSDocumentGraph();
		
		/* adding annotations to salt sample */
//		EList<SToken> sTokens = docGraph.getSTokens();
//		sTokens.get(0).createSAnnotation(null, TCFMapperImport.LEVEL_POS, "VBZ");
//		sTokens.get(1).createSAnnotation(null, TCFMapperImport.LEVEL_POS, "DT");
//		sTokens.get(2).createSAnnotation(null, TCFMapperImport.LEVEL_POS, "NN");
//		sTokens.get(3).createSAnnotation(null, TCFMapperImport.LEVEL_POS, "ABR");
//		sTokens.get(4).createSAnnotation(null, TCFMapperImport.LEVEL_POS, "JJ");
//		sTokens.get(5).createSAnnotation(null, TCFMapperImport.LEVEL_POS, "IN");
//		sTokens.get(6).createSAnnotation(null, TCFMapperImport.LEVEL_POS, "PRP");
//		sTokens.get(7).createSAnnotation(null, TCFMapperImport.LEVEL_POS, "VBZ");
//		sTokens.get(8).createSAnnotation(null, TCFMapperImport.LEVEL_POS, "TO");
//		sTokens.get(9).createSAnnotation(null, TCFMapperImport.LEVEL_POS, "VB");
//		sTokens.get(10).createSAnnotation(null, TCFMapperImport.LEVEL_POS, ".");
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
		
		/* start mapper */
		System.out.println(tmpOut);		
		this.getFixture().mapSDocument();
				
		/* test from testPrimaryData() */
		SDocumentGraph fixGraph = getFixture().getSDocument().getSDocumentGraph();
		
		assertNotNull(getFixture().getSDocument());
		assertNotNull(getFixture().getSDocument().getSDocumentGraph());
		assertEquals(docGraph.getSTextualDSs().size(), fixGraph.getSTextualDSs().size());		
		assertEquals(docGraph.getSTextualDSs().get(0).getSText().length(), fixGraph.getSTextualDSs().get(0).getSText().length());
		assertEquals(docGraph.getSTextualDSs().get(0).getSText(), fixGraph.getSTextualDSs().get(0).getSText());	
		
		/* tests from testTokens() */
		
		assertNotEquals(fixGraph.getSTextualDSs().size(), 0);
		assertNotEquals(fixGraph.getSTokens().size(), 0);
		assertEquals(docGraph.getSTokens().size(), fixGraph.getSTokens().size());		
		
		for(int i=0; i<docGraph.getSTextualRelations().size(); i++){
			assertEquals(docGraph.getSTextualRelations().get(i).getSStart(), fixGraph.getSTextualRelations().get(i).getSStart());
		}
		
		/* compare template salt model to imported salt model */	
		
		SToken tok;
		for(int i=0; i<fixGraph.getSTokens().size(); i++){			
			tok = fixGraph.getSTokens().get(i);
			assertNotEquals(tok.getSAnnotation(TCFMapperImport.LEVEL_POS), null);
			assertNotEquals(tok.getSAnnotation(TCFMapperImport.LEVEL_POS).getValue(), null);
			assertNotEquals(tok.getSAnnotation(TCFMapperImport.LEVEL_POS).getValue(), "");
			/* compare assuming the tokens are stored in their linear order (always true) */			
			assertEquals(tok.getSAnnotation(TCFMapperImport.LEVEL_POS).getValue(), docGraph.getSTokens().get(i).getSAnnotation(SaltSemanticsPackage.eNS_PREFIX+"::"+SALT_SEMANTIC_NAMES.POS.toString()).getValue());			
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
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement(TCFDictionary.TAG_TC_SENTENCES);
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_SENTENCE);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "s_0");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t1 t2 t3 t4 t5 t6 t7 t8 t9 t10");
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
		
		/* start mapper */
		System.out.println(tmpOut);		
		this.getFixture().mapSDocument();
		
		/* compare template salt model to imported salt model */
		SDocumentGraph fixGraph = getFixture().getSDocument().getSDocumentGraph();
		
		assertNotEquals(fixGraph.getSSpans().size(), 0);
		assertEquals(fixGraph.getSSpans().size(), docGraph.getSSpans().size());
		
		SSpan fixSpan = fixGraph.getSSpans().get(0);
		SSpan docSpan = docGraph.getSSpans().get(0);
		
		EList<STYPE_NAME> typeList = new BasicEList<STYPE_NAME>();
		typeList.add(STYPE_NAME.SSPANNING_RELATION);
		
//		assertEquals(true, fixGraph.getOverlappedSTokens(fixSpan, typeList).containsAll(docGraph.getOverlappedSTokens(docSpan, typeList)));
		
	}
	
	/**
	 * This method tests if a valid TCF-XML-structure containing lemmas
	 * and tokens is converted to salt correctly by {@link TCFMapperImport}  
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testTokensLemma() throws XMLStreamException, FileNotFoundException{
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
					xmlWriter.writeStartElement(TCFDictionary.TAG_TC_LEMMAS);
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_LEMMA);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt1");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t1");
						xmlWriter.writeCharacters("be");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_LEMMA);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt2");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t2");
						xmlWriter.writeCharacters("this");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_LEMMA);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt3");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t3");
						xmlWriter.writeCharacters("example");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_LEMMA);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt4");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t4");
						xmlWriter.writeCharacters("more");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_LEMMA);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt5");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t5");
						xmlWriter.writeCharacters("complicated");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_LEMMA);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt6");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t6");
						xmlWriter.writeCharacters("than");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_LEMMA);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt7");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t7");
						xmlWriter.writeCharacters("it");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_LEMMA);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt8");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t8");
						xmlWriter.writeCharacters("appear");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_LEMMA);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt9");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t9");
						xmlWriter.writeCharacters("to");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_LEMMA);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt10");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t10");
						xmlWriter.writeCharacters("be");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_LEMMA);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt11");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t11");
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
		SaltSample.createMorphologyAnnotations2(doc); //lemma'n'stuff
		SDocumentGraph docGraph = doc.getSDocumentGraph();
		
		/* adding lemma-annotations to salt sample */
//		EList<SToken> sTokens = docGraph.getSTokens();
//		sTokens.get(0).createSAnnotation(null, TCFMapperImport.LEVEL_LEMMA, "be");
//		sTokens.get(1).createSAnnotation(null, TCFMapperImport.LEVEL_LEMMA, "this");
//		sTokens.get(2).createSAnnotation(null, TCFMapperImport.LEVEL_LEMMA, "example");
//		sTokens.get(3).createSAnnotation(null, TCFMapperImport.LEVEL_LEMMA, "more");
//		sTokens.get(4).createSAnnotation(null, TCFMapperImport.LEVEL_LEMMA, "complicated");
//		sTokens.get(5).createSAnnotation(null, TCFMapperImport.LEVEL_LEMMA, "than");
//		sTokens.get(6).createSAnnotation(null, TCFMapperImport.LEVEL_LEMMA, "it");
//		sTokens.get(7).createSAnnotation(null, TCFMapperImport.LEVEL_LEMMA, "appear");
//		sTokens.get(8).createSAnnotation(null, TCFMapperImport.LEVEL_LEMMA, "to");
//		sTokens.get(9).createSAnnotation(null, TCFMapperImport.LEVEL_LEMMA, "be");		
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_TOKENS_LEMMA);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		this.getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		
		/* start mapper */
		System.out.println(tmpOut);		
		this.getFixture().mapSDocument();
		
		/* test from other methods */
		
		/**
		 * TODO
		 */
		
		/* compare template salt model to imported salt model */	
		SDocumentGraph fixGraph = getFixture().getSDocument().getSDocumentGraph();
		SToken tok;
		for(int i=0; i<fixGraph.getSTokens().size(); i++){
			tok = fixGraph.getSTokens().get(i);
			assertNotEquals(tok.getSAnnotation(TCFMapperImport.LEVEL_LEMMA), null);
			
			/* compare with the (always true) assumption that the tokens are stored in their linear order */			
			assertEquals(tok.getSAnnotation(TCFMapperImport.LEVEL_LEMMA), docGraph.getSTokens().get(i).getSAnnotation(TCFMapperImport.LEVEL_LEMMA));			
		}
	}
	
	/**
	 * This method tests if a valid TCF-XML-structure containing dependency
	 * annotations is converted to salt correctly by {@link TCFMapperImport} 
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testDepparsing() throws XMLStreamException, FileNotFoundException{
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
					xmlWriter.writeAttribute(TCFDictionary.ATT_TAGSET, "stanford");
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
							xmlWriter.writeAttribute(TCFDictionary.ATT_GOVIDS, "t_8");
							xmlWriter.writeAttribute(TCFDictionary.ATT_DEPIDS, "t_6");
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
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_TOKENS_LEMMA);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		this.getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		
		/* start mapper */
		System.out.println(tmpOut);		
		this.getFixture().mapSDocument();
		
		/* test from other methods */
		
		/**
		 * TODO (?)
		 */
		
		/* compare template salt model to imported salt model */	
		SDocumentGraph fixGraph = getFixture().getSDocument().getSDocumentGraph();
		SDocumentGraph docGraph = doc.getSDocumentGraph();
		EList<SPointingRelation> docPRels = docGraph.getSPointingRelations();
		EList<SPointingRelation> fixPRels = fixGraph.getSPointingRelations();
		
//		/* TEST PRINT */
//		System.out.println("doc:");
//		for(SPointingRelation pR : docPRels){
//			System.out.println(pR.toString());
//		}
//		System.out.println("fix:");
//		for(SPointingRelation pR : fixPRels){
//			System.out.println(pR.toString());
//		}
//		System.out.println("TOTAL: " + docPRels.size() + " (doc), " + fixPRels.size() + " (fixture)");		
//		/* END OF TEST PRINT */
		
		assertNotNull(fixPRels);
		assertNotEquals(0, fixPRels.size());		
		assertEquals(docPRels.size(), fixPRels.size());		
		for(int i=0; i<docPRels.size(); i++){
			/* compare type of dependency */
			assertEquals(docPRels.get(i).getSAnnotation(TCFMapperImport.LEVEL_DEPENDENCY), fixPRels.get(i).getSAnnotation(TCFMapperImport.LEVEL_DEPENDENCY));
			/* TODO compare source tokens (IMPROVE with bitset or similar) */
			SToken docSrc = (SToken)docPRels.get(i).getSSource();
			SToken fixSrc = (SToken)fixPRels.get(i).getSSource();
			if((fixSrc!=null)&(docSrc!=null)){
				int j=0;
				while(!docGraph.getSTextualRelations().get(j).getSource().equals(docSrc)){j++;}
				int k=0;
				while(!fixGraph.getSTextualRelations().get(k).getSource().equals(fixSrc)){k++;}
				String docTok = docGraph.getSTextualDSs().get(0).getSText().toString().substring(docGraph.getSTextualRelations().get(j).getSStart(), docGraph.getSTextualRelations().get(j).getSEnd());
				String fixTok = fixGraph.getSTextualDSs().get(0).getSText().toString().substring(fixGraph.getSTextualRelations().get(k).getSStart(), fixGraph.getSTextualRelations().get(k).getSEnd());
				/* TEST PRINT */
				System.out.println("dep="+ docPRels.get(i).getSAnnotation(TCFMapperImport.LEVEL_DEPENDENCY).getValueString()+"\tdep="+fixPRels.get(i).getSAnnotation(TCFMapperImport.LEVEL_DEPENDENCY).getValueString());
				System.out.println("docTok="+docTok+"\tfixTok="+fixTok);
				/* END OF TEST PRINT */
				assertEquals(docTok, fixTok);
				/* TODO compare target tokens */
				SToken docTarget = (SToken)docPRels.get(i).getSTarget();
				SToken fixTarget = (SToken)fixPRels.get(i).getSTarget();
				j=0;
				while(!docGraph.getSTextualRelations().get(j).getSource().equals(docTarget)){j++;}
				k=0;
				while(!fixGraph.getSTextualRelations().get(k).getSource().equals(fixTarget)){k++;}
				docTok = docGraph.getSTextualDSs().get(0).getSText().toString().substring(docGraph.getSTextualRelations().get(j).getSStart(), docGraph.getSTextualRelations().get(j).getSEnd());
				fixTok = fixGraph.getSTextualDSs().get(0).getSText().toString().substring(fixGraph.getSTextualRelations().get(k).getSStart(), fixGraph.getSTextualRelations().get(k).getSEnd());
				assertEquals(docTok, fixTok);
			}
		}
		
	}
	
	private enum TEST_MODE{
		TEXT, TOKEN, TOKEN_POS, SENTENCE, LEMMA, DEPENDENCY;
	}

}
