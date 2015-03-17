pepperModules-TCFModules
==================
### General information
TCFModules is part of the [SaltNPepper](https://korpling.german.hu-berlin.de/p/projects/saltnpepper/) project. This Pepper module allows the conversion of data provided in the [TCF format](http://weblicht.sfs.uni-tuebingen.de/weblichtwiki/index.php/The_TCF_Format) to Salt, a graph-based data model. TCF is a common XML data exchange format which has been developed within the [WebLicht](http://weblicht.sfs.uni-tuebingen.de/weblichtwiki/) architecture.
This module has been developed in cooperation of the [Clarin-D](http://de.clarin.eu/) center Universität Stuttgart and the Humboldt-Universität zu Berlin.  
[[read more]](http://korpling.github.io/pepperModules-TCFModules)
  
  
License
-------
Copyright 2014 Clarin-D, Humboldt-Universität zu Berlin.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Requirements
-------
Since the here provided module is a plugin for Pepper, you need an instance of the Pepper framework. If you do not already have a running Pepper instance, click on the link below and download the latest stable version (not a SNAPSHOT):
[SaltNPepper](http://korpling.german.hu-berlin.de/saltnpepper/repository/saltNpepper_full/)

Install TCFModules
-------
If this Pepper module is not yet contained in your Pepper distribution, you can easily install it. Just open a command line and start pepper.

**Windows**
```
pepperStart.bat 
```

**Linux/Unix**
```
bash pepperStart.sh 
```

Pepper will start in command line mode (the command line starts with `pepper>`). To install this module, type:
```
update pepperModules-TCFModules
``` 
If your Pepper instance is configured for TCFModules, the update process will start. If not, you will read something like this:
```
pepperModules-TCFModules is not a known module.
``` 
To use pepperModules-TCFModules nevertheless, you can start the installation by adding an entry to Pepper's update list with the following command:
```
update de.hu_berlin.german.korpling.saltnpepper::pepperModules-TCFModules::https://korpling.german.hu-berlin.de/maven2 
``` 
Note, that the update is performed directly after typing that. From then on you will be able to start updates of pepperModules-TCFModules with `update pepperModules-TCFModules`.

## Usage
To use this module in your Pepper workflow, put the following lines into the workflow description file. Note the fixed order of xml elements in the workflow description file: &lt;importer/&gt, &lt;manipulator/&gt, &lt;exporter/&gt.
A detailed description of the Pepper workflow can be found on the [Pepper project site](https://github.com/korpling/pepper). 

### a) Identify the module by name

```xml
<importer name="TCFImporter" path="PATH_TO_CORPUS"/>
``` 
or
```xml
<exporter name="TCFExporter" path="PATH_TO_CORPUS"/>
``` 

### c) Use properties
```xml
<importer name="TCFImporter" path="PATH_TO_CORPUS">
  <customization>
    <property key="PROPERTY-1_NAME">PROPERTY-1_VALUE</key>
    <property key="PROPERTY-2_NAME">PROPERTY-2_VALUE</key>
    ...
  </customization>
</importer>
``` 

## Contribute
Since this Pepper module is under a free license, please feel free to fork it from github and improve the module. If you even think that others can benefit from your improvements, don't hesitate to make a pull request, so that your changes can be merged.
If you have found any bugs, or have some feature request, please open an issue on github. If you need any help, please write an e-mail to saltnpepper@lists.hu-berlin.de.

## Funders
This project was funded by the [Clarin-D project](http://www.clarin-d.de/) and realized at the [department of corpus linguistics and morphology](http://www.linguistik.hu-berlin.de/institut/professuren/korpuslinguistik/) of the Humboldt Universität.

# TCFImporter

## Mapping to Salt

The importer maps each TCF layer to an SLayer object in Salt. Attributes refering to the
TCF layer are stored as meta annotations on the SLayer itself without using a
namespace. All annotations contained in the TCF layer are imported as annotations on
SToken and/or SSpan objects where the annotation's namespace is (in most cases, cf.
table below) the SLayer's name and the annotation's name is the tag's name without
the xml namespace or the attributes name. E.g.:
```xml
<constituent cat="VVFIN" ID="c_1" tokenIDs="t_1"/>
``` 
In this case the token is annotated with syntax::cat="VVFIN" in the salt model. An overview of all TCF-Layers and according SLayers can be found in the following table:
|TCF layer|namespace of annotation|node annotation names|edge annotation names|meta annotations on SLayer|
|---------|-----------------------|---------------------|---------------------|--------------------------|
| POSTags | saltSemantics         | POS                 |                     | tagset                   |
| lemmas  | saltSemantics | LEMMA | | | 
| parsing | syntax | cat | | tagset | 
| depparsing | dependencies | | func | tagset | 
| morphology | morphology | * | | | 
| namedEntities | named entities | class | | type | 
| references | references | type | rel | typetagset, reltagset | 
| synonymy | lexical-semantics | | | synonymy (on lemma annotation) | 
| antonymy             | lexical-semantics | | | antonymy (on lemma annotation) | 
| hyponymy             | lexical-semantics | | | hyponymy (on lemma annotation) | 
| hyperonymy           | lexical-semantics | | | hyperonymy (on lemma annotation) | 
| wsd                  | wordSense | lexunits, comment | | src | 
| WordSplittings       | wordSplittings | split | | type | 
| geo                  | geography | alt, lat, lon, continent, country, capital | | src | 
| discourseconnectives | discourseConnectives | type | | tagset | 
| phonetics            | phonetics | pron | | transcription | 
| textstructure        | textstructure | type | | | 
| orthography          | orthography   | correction | | |

## Properties
The table below contains an overview of all usable properties to customize the behaviour of this Pepper module. The following section contains a close description to each single property and describes the resulting differences in the mapping to the Salt model.
| Name of property | Type of property | optional/mandatory | default value |
|------------------|------------------|--------------------|---------------|
| shrinkTokenAnnotations | Boolean | optional | true |

### shrinkTokenAnnotations
This property influences the import of annotations on single tokens. If it is set true, annotations on single tokens are stored as annotations directly at the token object, whereas a span is build over all tokens for multiple token annotations. If shrinkTokenAnnotations is set false, also annotations of single tokens are created at a span built over the token.

# TCFExporter

## Mapping from Salt
Each STextualDS in an SDocument is mapped to a single TCF file. In case of multiple STextualDSs the files names will be $DocumentName.[0–9]+.tcf.
In the current state the exporter is capable of mapping primary text, tokens, sentences, POS and lemma annotations, which are the basic features
for further processing in WebLicht, which TCF was also made for. To enable the exporter to do this, default assumptions about annotations QNames
and values are made, which can be overriden by properties.

## Properties
The table below contains an overview of all usable properties to customize the behaviour of this Pepper module. The following section contains a close description to each single property and describes the resulting differences in the mapping to TCF.
| Name of property | Type of property | optional/mandatory | default value |
|------------------|------------------|--------------------|---------------|
| allow.emptyTokens | Boolean | optional | true |
| pos.qname | String | optional | "POS" |
| lemma.qname | String | optional | "LEMMA" |
| sentence.qname | String | optional | "sentence" |
| sentence.value | String | optional | "sentence" |
| textstructure.line.qname | String | optional | "textstructure" |
| textstructure.line.value | String | optional | "line" |
| textstructure.page.qname | String | optional | "textstructure" |
| textstructure.page.value | String | optional | "page" |

### allow.emptyTokens
Some importers create SToken objects without any textual content. By setting this property to false, these tokens will be ignored in the export process.

### pos.qname
This property contains the QName of part of speech annotations (namespace+"::"+name or simply name if namespace==null).

### lemma.qname
This property contains the QName of lemma annotations.

### sentence.qname
This property contains the QName of SAnnotations marking sentence spans.

### sentence.value
This property contains the value of SAnnotations marking sentence spans.

### textstructure.line.qname
This property contains the QName of SAnnotations marking spans containing tokens that form a line.
### textstructure.line.value
This property contains the value of SAnnotations marking spans containing tokens that form a line.
### textstructure.page.qname
This property contains the QName of SAnnotations marking spans containing tokens that form a page.
### textstructure.page.value
This property contains the value of SAnnotations marking spans containing tokens that form a page.
