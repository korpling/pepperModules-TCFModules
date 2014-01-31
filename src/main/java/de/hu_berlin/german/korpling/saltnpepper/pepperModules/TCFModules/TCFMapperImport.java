package de.hu_berlin.german.korpling.saltnpepper.pepperModules.TCFModules;

import org.eclipse.emf.common.util.URI;
import org.osgi.service.log.LogService;

import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.MAPPING_RESULT;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperMapper;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperModuleProperties;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.impl.PepperMapperImpl;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpus;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;

public class TCFMapperImport extends PepperMapperImpl{
//CHECK-ASK: might it be better to implement and extension of PepperMapperImpl here?	
	
	@Override
	public MAPPING_RESULT mapSCorpus() {
		//returns the resource in case that a module is an importer or exporter
		getResourceURI();
		//returns the SDocument object to be manipulated
		getSDocument();
		//returns that the process was successful
		return(MAPPING_RESULT.FINISHED);
		
	}
	@Override
	public MAPPING_RESULT mapSDocument() {
		//returns the resource in case that the module is an importer or exporter
		getResourceURI();
		//returns the SCorpus object to be manipulated
		getSCorpus();//?
		//returns that the process was successful
		return(MAPPING_RESULT.FINISHED);
	}

}
