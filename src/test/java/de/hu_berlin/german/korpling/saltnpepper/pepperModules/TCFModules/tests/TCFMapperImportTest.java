package de.hu_berlin.german.korpling.saltnpepper.pepperModules.TCFModules.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Stack;

import javax.xml.stream.FactoryConfigurationError;
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

import de.hu_berlin.german.korpling.saltnpepper.pepperModules.tcfModules.TCFDictionary;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.tcfModules.TCFImporterProperties;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.tcfModules.TCFMapperImport;
import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Label;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Node;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDataSourceSequence;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDominanceRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SPointingRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpanningRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructure;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STYPE_NAME;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SGraphTraverseHandler;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SLayer;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SaltCoreFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltSample.SaltSample;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltSemantics.SALT_SEMANTIC_NAMES;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltSemantics.SaltSemanticsFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltSemantics.SaltSemanticsPackage;

public class TCFMapperImportTest {

	private TCFMapperImport fixture;
	private static final String EXAMPLE_TEXT = "Is this example more complicated than it appears to be?";
	private static final String EXAMPLE_TEXT_SHRINK = "I love New York.";
	private static final String LOCATION_TEST_PRIMARY_TEXT = "/pepper-test/tcfImporterTestPrimaryData.xml";
	private static final String LOCATION_TEST_TOKENS = "/pepper-test/tcfImporterTestTokens.xml";
	private static final String LOCATION_TEST_TOKENS_POS = "/pepper-test/tcfImporterTestTokensPos.xml";
	private static final String LOCATION_TEST_TOKENS_LEMMA = "/pepper-test/tcfImporterTestTokensLemma.xml";
	private static final String LOCATION_TEST_SENTENCE = "/pepper-test/tcfImporterTestSentence.xml";
	private static final String LOCATION_TEST_DEPENDENCIES_NO_MULTIGOVS = "/pepper-test/tcfImporterTestDependenciesNoMultigovs.xml";
	private static final String LOCATION_TEST_CONSTITUENT_PARSING = "/pepper-test/tcfImporterTestConstituentParsing.xml";
	private static final String LOCATION_TEST_MORPHOLOGY = "/pepper-test/tcfImporterTestMorphology.xml";
	private static final String LOCATION_TEST_REFERENCES = "/pepper-test/tcfImporterTestReferences.xml";
	
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
					xmlWriter.writeAttribute(TCFDictionary.ATT_TAGSET, "penn treebank");
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
						xmlWriter.writeCharacters("RBR");/*TODO wrong tag "ABR", does not exist in penn treebank – RBR (?) –– also fix in salt sample*/
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
		
		SLayer posLayer = SaltFactory.eINSTANCE.createSLayer();
		posLayer.setSName(TCFMapperImport.LAYER_POS);
		posLayer.getSNodes().addAll(docGraph.getSTokens());
		docGraph.addSLayer(posLayer);
		
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
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, true);
				
		/* test from testPrimaryData() */
		SDocumentGraph fixGraph = getFixture().getSDocument().getSDocumentGraph();
		SLayer fixLayer = fixGraph.getSLayerByName(TCFMapperImport.LAYER_POS).get(0);
		
		assertNotNull(getFixture().getSDocument());
		assertNotNull(getFixture().getSDocument().getSDocumentGraph());
		assertEquals(docGraph.getSTextualDSs().size(), fixGraph.getSTextualDSs().size());		
		assertEquals(docGraph.getSTextualDSs().get(0).getSText().length(), fixGraph.getSTextualDSs().get(0).getSText().length());
		assertEquals(docGraph.getSTextualDSs().get(0).getSText(), fixGraph.getSTextualDSs().get(0).getSText());	
		
		/* tests from testTokens() */
		
		assertNotEquals(fixGraph.getSTextualDSs().size(), 0);
		assertNotEquals(fixGraph.getSTokens().size(), 0);
		assertEquals(docGraph.getSTokens().size(), fixGraph.getSTokens().size());
		assertNotNull(fixLayer);
		assertEquals(posLayer.getSNodes().size(), fixLayer.getSNodes().size());
		
		for(int i=0; i<docGraph.getSTextualRelations().size(); i++){
			assertEquals(docGraph.getSTextualRelations().get(i).getSStart(), fixGraph.getSTextualRelations().get(i).getSStart());
		}
		
		/* compare template salt model to imported salt model */	
		
		SToken tok;
		String posQName = SaltSemanticsPackage.eNS_PREFIX+"::"+SALT_SEMANTIC_NAMES.POS.toString();
		for(int i=0; i<fixGraph.getSTokens().size(); i++){			
			tok = fixGraph.getSTokens().get(i);
			assertNotNull(tok.getSAnnotation(posQName));
			assertNotNull(tok.getSAnnotation(posQName).getValue());
			assertNotEquals(tok.getSAnnotation(posQName).getValue(), "");
			/* compare assuming the tokens are stored in their linear order (always true) */			
			assertEquals(tok.getSAnnotation(posQName).getValue(), docGraph.getSTokens().get(i).getSAnnotation(posQName).getValue());			
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
		
		/* start mapper */
		System.out.println(tmpOut);		
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
		
		/* start mapper */
		System.out.println(tmpOut);		
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
			assertEquals(docGraph.getSSpans().get(i).getSAnnotation(posQName), fixGraph.getSSpans().get(i).getSAnnotation(posQName));
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


		EList<SToken> docSpanTokens = docGraph.getOverlappedSTokens(docSpan, typeList);
		EList<SToken> fixSpanTokens = fixGraph.getOverlappedSTokens(fixSpan, typeList);
		for(int i=0; i<docSpanTokens.size(); i++){
		/*TEST*/System.out.println(docGraph.getSText(docSpanTokens.get(i))+" =?= "+fixGraph.getSText(fixSpanTokens.get(i)));
			assertEquals(docGraph.getSText(docSpanTokens.get(i)), fixGraph.getSText(fixSpanTokens.get(i)));
		}
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
		String lemmaQName = SaltSemanticsPackage.eNS_PREFIX+"::"+SALT_SEMANTIC_NAMES.LEMMA.toString();
		for(int i=0; i<fixGraph.getSTokens().size(); i++){
			tok = fixGraph.getSTokens().get(i);
			assertNotNull(tok.getSAnnotation(lemmaQName));
			
			/* compare with the (always true) assumption that the tokens are stored in their linear order */			
			assertEquals(tok.getSAnnotation(lemmaQName), docGraph.getSTokens().get(i).getSAnnotation(lemmaQName));			
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
		System.out.println("=======================================");
		System.out.println("TESTING LEMMA WITH SHRINKED ANNOTATIONS");
		System.out.println("=======================================");
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
					xmlWriter.writeStartElement(TCFDictionary.TAG_TC_LEMMAS);
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_LEMMA);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt1");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t1");
						xmlWriter.writeCharacters("I");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_LEMMA);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt2");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t2");
						xmlWriter.writeCharacters("love");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_LEMMA);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt3");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t3 t4");
						xmlWriter.writeCharacters("New York");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_LEMMA);
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
		
		/* start mapper */
		System.out.println(tmpOut);			
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
		System.out.println("===========================================");
		System.out.println("TESTING LEMMA WITH ANNOTATIONS NOT SHRINKED");
		System.out.println("===========================================");
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
					xmlWriter.writeStartElement(TCFDictionary.TAG_TC_LEMMAS);
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_LEMMA);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt1");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t1");
						xmlWriter.writeCharacters("I");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_LEMMA);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt2");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t2");
						xmlWriter.writeCharacters("love");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_LEMMA);
						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "pt3");
						xmlWriter.writeAttribute(TCFDictionary.ATT_TOKENIDS, "t3 t4");
						xmlWriter.writeCharacters("New York");
						xmlWriter.writeEndElement();
						xmlWriter.writeStartElement(TCFDictionary.TAG_TC_LEMMA);
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
		
		/* start mapper */
		System.out.println(tmpOut);			
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
			/*TEST*/System.out.println("[doc]Node \""+docGraph.getSText(docNode)+"\" is instance of "+docNode.getClass().getSimpleName());
			fixNode = fixLemma.get(i);
			/*TEST*/System.out.println("[fix]Node \""+fixGraph.getSText(fixNode)+"\" is instance of "+fixNode.getClass().getSimpleName());
			/* instance of class Span? */
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
		
		/* start mapper */
		System.out.println(tmpOut);		
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
				assertEquals(docPRels.get(j).getSAnnotation(TCFMapperImport.LEVEL_DEPENDENCY).getSValue(), fixPRels.get(j).getSAnnotation(TCFMapperImport.LEVEL_DEPENDENCY).getSValue());				
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
		
		/* start mapper */
		System.out.println(tmpOut);		
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
//			System.out.println(i+"\tdoc="+docNodes.get(i).getSElementId().toString().replace("de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.impl.SElementIdImpl@", "")+"\t"+docNodes.get(i).getSAnnotation(TCFMapperImport.ANNO_NAME_CONSTITUENT).getValueString());
//			System.out.println("\t"+docGraph.getSText(docNodes.get(i)));
//			System.out.println(i+"\tfix="+fixNodes.get(i).getSElementId().toString().replace("de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.impl.SElementIdImpl@", "")+"\t"+fixNodes.get(i).getSAnnotation(TCFDictionary.ATT_CAT).getValueString());
//			System.out.println("\t"+fixGraph.getSText(fixNodes.get(i)));
			assertEquals(docNodes.get(i).getSElementId(), fixNodes.get(i).getSElementId());
			assertEquals(docGraph.getSText(docNodes.get(i)), fixGraph.getSText(fixNodes.get(i)));
			assertEquals(docNodes.get(i).getSAnnotation(TCFMapperImport.ANNO_NAME_CONSTITUENT).getValue(), fixNodes.get(i).getSAnnotation(TCFDictionary.ATT_CAT).getValue());
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
		System.out.println("================================================");
		System.out.println("TESTING MORPHOLOGY WITH ANNOTATIONS NOT SHRINKED");
		System.out.println("================================================");
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
		sSpan.createSAnnotation(null, "cat", "verb");
		sSpan.createSAnnotation(null, "person", "3");
		sSpan.createSAnnotation(null, "number", "singular");
		sSpan.createSAnnotation(null, "tense", "present");
		sSpan.createSAnnotation(null, "indicative", "true");
		sSpan.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
		//in this last annotation (storage of segment.type) we use a namespace to avoid ambiguities
		//with potential morphological properties used in <analysis>...</analysis> (therefore namespace = TAG_TC_SEGMENT)
		sSpan.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "be");
		//the character sequence between <segment>...</segment> will be represented in the same namespace and with key=TAG(=namespace)
		//I'm not sure I like that
		docMorph.add(sSpan);
		
		sSpan = docGraph.createSSpan(docTokens.get(1));//this
		sSpan.createSAnnotation(null, "cat", "determiner");
		sSpan.createSAnnotation(null, "number", "singular");
		sSpan.createSAnnotation(null, "definiteness", "true");
		sSpan.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
		docMorph.add(sSpan);
		
		sSpan = docGraph.createSSpan(docTokens.get(2));//example
		sSpan.createSAnnotation(null, "cat", "noun");
		sSpan.createSAnnotation(null, "number", "singular");
		sSpan.createSAnnotation(null, "gender", "neuter");
		sSpan.createSAnnotation(null, "case", "nominative");
		sSpan.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
		sSpan.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "example");
		docMorph.add(sSpan);
		
		EList<SToken> spanTokens = new BasicEList<SToken>();
		spanTokens.add(docTokens.get(3));
		spanTokens.add(docTokens.get(4));
		
		sSpan = docGraph.createSSpan(spanTokens);//more complicated		
		sSpan.createSAnnotation(null, "cat", "adjective");
		sSpan.createSAnnotation(null, "comparative", "true");
		sSpan.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
		sSpan.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "complicated");
		docMorph.add(sSpan);
		
		spanTokens.clear();
		
		sSpan = docGraph.createSSpan(docTokens.get(5));//than
		sSpan.createSAnnotation(null, "cat", "conjunction");
		sSpan.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
		sSpan.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "than");
		docMorph.add(sSpan);		
		
		sSpan = docGraph.createSSpan(docTokens.get(6));//it
		sSpan.createSAnnotation(null, "cat", "personal pronoun");
		sSpan.createSAnnotation(null, "number", "singular");
		sSpan.createSAnnotation(null, "person", "3");
		sSpan.createSAnnotation(null, "gender", "neuter");
		sSpan.createSAnnotation(null, "case", "nominative");
		sSpan.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
		sSpan.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "it");
		docMorph.add(sSpan);
		
		sSpan = docGraph.createSSpan(docTokens.get(7));//appears
		sSpan.createSAnnotation(null, "cat", "verb");
		sSpan.createSAnnotation(null, "person", "3");
		sSpan.createSAnnotation(null, "number", "singular");
		sSpan.createSAnnotation(null, "tense", "present");
		sSpan.createSAnnotation(null, "indicative", "true");
		sSpan.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
		sSpan.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "appear");
		docMorph.add(sSpan);
		
		spanTokens.add(docTokens.get(8));
		spanTokens.add(docTokens.get(9));
		
		sSpan = docGraph.createSSpan(spanTokens);//to be
		sSpan.createSAnnotation(null, "cat", "verb");
		sSpan.createSAnnotation(null, "infinitive", "true");
		sSpan.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
		sSpan.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "be");
		docMorph.add(sSpan);
		
		spanTokens.clear();
		
		sSpan = docGraph.createSSpan(docTokens.get(10));//?
		sSpan.createSAnnotation(null, "cat", "punctuation");
		sSpan.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
		sSpan.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, ".");
		docMorph.add(sSpan);
		
		/* setting variables */		
		File tmpOut = new File(System.getProperty("java.io.tmpdir")+LOCATION_TEST_MORPHOLOGY);
		tmpOut.getParentFile().mkdirs();
		PrintWriter p = new PrintWriter(tmpOut);		
		p.println(outStream.toString());
		p.close();
		this.getFixture().setResourceURI(URI.createFileURI(tmpOut.getAbsolutePath()));
		this.getFixture().getProperties().setPropertyValue(TCFImporterProperties.PROP_SHRINK_TOKEN_ANNOTATIONS, false);
		
		/* start mapper */
		System.out.println(tmpOut);		
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
			/*TEST*/System.out.println("[doc]Node(s):\t\""+docGraph.getSText(docNode)+"\" instance of "+docNode.getClass().getSimpleName());
			/*TEST*/System.out.println("[fix]Node(s):\t\""+fixGraph.getSText(fixNode)+"\" instance of "+fixNode.getClass().getSimpleName());
			/* fixNode of type SSpan? */
			assertTrue(fixNode instanceof SSpan);
			/* both overlap the same SText? */
			assertEquals(docGraph.getSText(docNode), fixGraph.getSText(fixNode));
			for(SAnnotation sAnno : docNode.getSAnnotations()){
				String qName = sAnno.getQName();
				/* compare annotations */
				/*TEST*/System.out.println("[doc]"+qName+"="+sAnno.getValueString());
				/*TEST*/System.out.println("[fix]"+qName+"="+fixNode.getSAnnotation(qName).getValue());
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
		System.out.println("============================================");
		System.out.println("TESTING MORPHOLOGY WITH SHRINKED ANNOTATIONS");
		System.out.println("============================================");
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
		
		sNode.createSAnnotation(null, "cat", "verb");//Is
		sNode.createSAnnotation(null, "person", "3");
		sNode.createSAnnotation(null, "number", "singular");
		sNode.createSAnnotation(null, "tense", "present");
		sNode.createSAnnotation(null, "indicative", "true");
		sNode.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
		//in this last annotation (storage of segment.type) we use a namespace to avoid ambiguities
		//with potential morphological properties used in <analysis>...</analysis> (therefore namespace = TAG_TC_SEGMENT)
		sNode.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "be");
		//the character sequence between <segment>...</segment> will be represented in the same namespace and with key=TAG(=namespace)
		//I'm not sure I like that
		docMorph.add(sNode);
		
		sNode = docTokens.get(1);//this
		sNode.createSAnnotation(null, "cat", "determiner");
		sNode.createSAnnotation(null, "number", "singular");
		sNode.createSAnnotation(null, "definiteness", "true");
		sNode.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
		docMorph.add(sNode);
		
		sNode = docTokens.get(2);//example
		sNode.createSAnnotation(null, "cat", "noun");
		sNode.createSAnnotation(null, "number", "singular");
		sNode.createSAnnotation(null, "gender", "neuter");
		sNode.createSAnnotation(null, "case", "nominative");
		sNode.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
		sNode.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "example");
		docMorph.add(sNode);
		
		EList<SToken> spanTokens = new BasicEList<SToken>();
		spanTokens.add(docTokens.get(3));
		spanTokens.add(docTokens.get(4));
		
		sNode = docGraph.createSSpan(spanTokens);//more complicated		
		sNode.createSAnnotation(null, "cat", "adjective");
		sNode.createSAnnotation(null, "comparative", "true");
		sNode.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
		sNode.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "complicated");
		docMorph.add(sNode);
		
		spanTokens.clear();
		
		sNode = docTokens.get(5);//than
		sNode.createSAnnotation(null, "cat", "conjunction");
		sNode.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
		sNode.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "than");
		docMorph.add(sNode);		
		
		sNode = docTokens.get(6);//it
		sNode.createSAnnotation(null, "cat", "personal pronoun");
		sNode.createSAnnotation(null, "number", "singular");
		sNode.createSAnnotation(null, "person", "3");
		sNode.createSAnnotation(null, "gender", "neuter");
		sNode.createSAnnotation(null, "case", "nominative");
		sNode.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
		sNode.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "it");
		docMorph.add(sNode);
		
		sNode = docTokens.get(7);//appears
		sNode.createSAnnotation(null, "cat", "verb");
		sNode.createSAnnotation(null, "person", "3");
		sNode.createSAnnotation(null, "number", "singular");
		sNode.createSAnnotation(null, "tense", "present");
		sNode.createSAnnotation(null, "indicative", "true");
		sNode.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
		sNode.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "appear");
		docMorph.add(sNode);
		
		spanTokens.add(docTokens.get(8));
		spanTokens.add(docTokens.get(9));
		
		sNode = docGraph.createSSpan(spanTokens);//to be
		sNode.createSAnnotation(null, "cat", "verb");
		sNode.createSAnnotation(null, "infinitive", "true");
		sNode.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
		sNode.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, "be");
		docMorph.add(sNode);
		
		spanTokens.clear();
		
		sNode = docTokens.get(10);//?
		sNode.createSAnnotation(null, "cat", "punctuation");
		sNode.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.ATT_TYPE, "stem");
		sNode.createSAnnotation(TCFDictionary.TAG_TC_SEGMENT, TCFDictionary.TAG_TC_SEGMENT, ".");
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
		System.out.println(tmpOut);		
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
			/*TEST*/System.out.println("[doc]Node(s):\t\""+docGraph.getSText(docNode)+"\" instance of "+docNode.getClass().getSimpleName());
			/*TEST*/System.out.println("[fix]Node(s):\t\""+fixGraph.getSText(fixNode)+"\" instance of "+fixNode.getClass().getSimpleName());
			/* both of the same type? */
			assertEquals(docNode.getClass(), fixNode.getClass());
			assertTrue((fixNode instanceof SToken)||(fixNode instanceof SSpan));//necessary?
			assertEquals(docGraph.getSText(docNode), fixGraph.getSText(fixNode));
			for(SAnnotation sAnno : docNode.getSAnnotations()){
				String qName = sAnno.getQName();
				/* compare annotations */
				/*TEST*/System.out.println("[doc]"+qName+"="+sAnno.getValueString());
				/*TEST*/System.out.println("[fix]"+qName+"="+fixNode.getSAnnotation(qName).getValue());
				assertNotNull(fixNode.getSAnnotation(qName));				
				assertEquals(sAnno.getValue(), fixNode.getSAnnotation(qName).getValue());
			}
			/*TODO Segment*/
		}
		
	}
//	
//	/**This method tests if a valid TCF-XML-structure containing named entity
//	 * annotations is converted to salt correctly by {@link TCFMapperImport} 
//	 * @throws XMLStreamException 
//	 */
//	@Test
//	public void testNamedEntities() throws XMLStreamException{
//		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//		XMLOutputFactory o= XMLOutputFactory.newFactory();
//		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);
//	}
	
//	/**This method tests if a valid TCF-XML-structure containing reference
//	 * annotations is converted to salt correctly by {@link TCFMapperImport} 
//	 * @throws XMLStreamException 
//	 */
//	@Test
//	public void testReferences() throws XMLStreamException{
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
//					xmlWriter.writeCharacters(EXAMPLE_TEXT);
//				xmlWriter.writeEndElement();
//				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKENS, TCFDictionary.NS_VALUE_TC);
//					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t1");
//						xmlWriter.writeCharacters("Is");
//					xmlWriter.writeEndElement();
//					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t2");
//						xmlWriter.writeCharacters("this");
//					xmlWriter.writeEndElement();
//					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t3");
//						xmlWriter.writeCharacters("example");
//					xmlWriter.writeEndElement();
//					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t4");
//						xmlWriter.writeCharacters("more");
//					xmlWriter.writeEndElement();
//					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t5");
//						xmlWriter.writeCharacters("complicated");
//					xmlWriter.writeEndElement();
//					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t6");
//						xmlWriter.writeCharacters("than");
//					xmlWriter.writeEndElement();
//					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t7");
//						xmlWriter.writeCharacters("it");
//					xmlWriter.writeEndElement();
//					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t8");
//						xmlWriter.writeCharacters("appears");
//					xmlWriter.writeEndElement();
//					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t9");
//						xmlWriter.writeCharacters("to");
//					xmlWriter.writeEndElement();
//					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t10");
//						xmlWriter.writeCharacters("be");
//					xmlWriter.writeEndElement();
//					xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_TOKEN, TCFDictionary.NS_VALUE_TC);
//						xmlWriter.writeAttribute(TCFDictionary.ATT_ID, "t11");
//						xmlWriter.writeCharacters("?");
//					xmlWriter.writeEndElement();
//				xmlWriter.writeEndElement();
//				xmlWriter.writeStartElement(TCFDictionary.NS_TC, TCFDictionary.TAG_TC_REFERENCES, TCFDictionary.NS_VALUE_TC);
//					
//				xmlWriter.writeEndElement();
//			xmlWriter.writeEndElement();
//		xmlWriter.writeEndElement();
//	xmlWriter.writeEndDocument();
//	}
	
//	/**This method tests if a valid TCF-XML-structure containing lexical-semantic
//	 * annotations is converted to salt correctly by {@link TCFMapperImport} 
//	 * @throws XMLStreamException 
//	 */
//	@Test
//	public void testLexicalSemantics() throws XMLStreamException{
//		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//		XMLOutputFactory o= XMLOutputFactory.newFactory();
//		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);
//	}
//	
//	/**This method tests if a valid TCF-XML-structure containing TCF matches
//	 * is converted to salt correctly by {@link TCFMapperImport} 
//	 * @throws XMLStreamException 
//	 */
//	@Test
//	public void testMatches() throws XMLStreamException{
//		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//		XMLOutputFactory o= XMLOutputFactory.newFactory();
//		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);
//	}
//	
//	/**This method tests if a valid TCF-XML-structure containing word splitting
//	 * annotations is converted to salt correctly by {@link TCFMapperImport} 
//	 * @throws XMLStreamException 
//	 */
//	@Test
//	public void testWordSplittings() throws XMLStreamException{
//		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//		XMLOutputFactory o= XMLOutputFactory.newFactory();
//		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);
//	}
//	
//	/**This method tests if a valid TCF-XML-structure containing geo data
//	 * annotations is converted to salt correctly by {@link TCFMapperImport} 
//	 * @throws XMLStreamException 
//	 */
//	@Test
//	public void testGeographicalLocations() throws XMLStreamException{
//		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//		XMLOutputFactory o= XMLOutputFactory.newFactory();
//		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);
//	}
//	
//	/**This method tests if a valid TCF-XML-structure containing discourse
//	 * annotations is converted to salt correctly by {@link TCFMapperImport} 
//	 * @throws XMLStreamException 
//	 */
//	@Test
//	public void testDiscourseConnectives() throws XMLStreamException{
//		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//		XMLOutputFactory o= XMLOutputFactory.newFactory();
//		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);
//	}
//	
//	/**This method tests if a valid TCF-XML-structure containing phonetic
//	 * transcriptions is converted to salt correctly by {@link TCFMapperImport} 
//	 * @throws XMLStreamException 
//	 */
//	@Test
//	public void testPhonetics() throws XMLStreamException{
//		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//		XMLOutputFactory o= XMLOutputFactory.newFactory();
//		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);
//	}
//	
//	/**This method tests if a valid TCF-XML-structure containing text structure
//	 * annotations is converted to salt correctly by {@link TCFMapperImport} 
//	 * @throws XMLStreamException 
//	 */
//	@Test
//	public void testTextStructure() throws XMLStreamException{
//		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//		XMLOutputFactory o= XMLOutputFactory.newFactory();
//		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);
//	}
//	
//	/**This method tests if a valid TCF-XML-structure containing orthography
//	 * annotations is converted to salt correctly by {@link TCFMapperImport} 
//	 * @throws XMLStreamException 
//	 */
//	@Test
//	public void testOrthography() throws XMLStreamException{
//		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//		XMLOutputFactory o= XMLOutputFactory.newFactory();
//		XMLStreamWriter xmlWriter= o.createXMLStreamWriter(outStream);
//	}	

	
	private enum TEST_MODE{
		TEXT, TOKEN, TOKEN_POS, SENTENCE, LEMMA, DEPENDENCY;
	}

}
