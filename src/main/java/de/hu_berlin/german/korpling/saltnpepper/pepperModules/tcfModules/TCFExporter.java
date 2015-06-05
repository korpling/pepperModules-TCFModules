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

import org.osgi.service.component.annotations.Component;

import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperExporter;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperMapper;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.impl.PepperExporterImpl;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SElementId;

@Component(name="TCFExporterComponent", factory="PepperExporterComponentFactory")
public class TCFExporter extends PepperExporterImpl implements PepperExporter{
	public TCFExporter(){
		super();
		this.setExportMode(EXPORT_MODE.DOCUMENTS_IN_FILES);
		this.setName("TCFExporter");
		this.addSupportedFormat("TCF", "0.4", null);
		this.setProperties(new TCFExporterProperties());
	}
	
	@Override
	public PepperMapper createPepperMapper(SElementId sElementId) {
		TCFMapperExport mapper = new TCFMapperExport();		
		if (sElementId.getSIdentifiableElement() instanceof SDocument) {
			mapper.setResourceURI(getSElementId2ResourceTable().get(sElementId));
		}		
		return mapper;
	}
}
