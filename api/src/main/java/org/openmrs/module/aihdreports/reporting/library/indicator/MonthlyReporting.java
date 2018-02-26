package org.openmrs.module.aihdreports.reporting.library.indicator;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.module.aihdreports.reporting.library.cohort.CommonCohortLibrary;
import org.openmrs.module.aihdreports.reporting.library.cohort.MonthlyReportingCohort;
import org.openmrs.module.aihdreports.reporting.metadata.Dictionary;
import org.openmrs.module.aihdreports.reporting.utils.CoreUtils;
import org.openmrs.module.aihdreports.reporting.utils.ReportUtils;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.aihdreports.reporting.utils.EmrReportingUtils.cohortIndicator;

@Component
public class MonthlyReporting {

    @Autowired
    private MonthlyReportingCohort cohorts;

    @Autowired
    private CommonCohortLibrary common;

    /**
     * Number of diabetic patients
     * @return CohortIndicator
     */
    public CohortIndicator numberOfDiabeticPatients(){
        Concept diabeticQuestion = Dictionary.getConcept(Dictionary.DIABETIC_VISIT_TYPE);
        Concept newDmPatient = Dictionary.getConcept(Dictionary.NEW_DIABETIC_PATIENT);
        Concept knownDmPatient = Dictionary.getConcept(Dictionary.KNOWN_DAIBETIC_PATIENT);
        return cohortIndicator("Diabetic Patients", ReportUtils.map(common.hasObs(diabeticQuestion, newDmPatient, knownDmPatient), "onOrAfter=${startDate},onOrBefore=${endDate}"));
    }

    /**
     * Number of patients who have encounters
     * @return CohortIndicator
     */
    public CohortIndicator numberOfPatientsWithEncounter(String uuid){
        EncounterType type = CoreUtils.getEncounterType(uuid);
        return cohortIndicator("Patients with encounter", ReportUtils.map(common.hasEncounter(type), "onOrAfter=${startDate},onOrBefore=${endDate}"));
    }

    /**
     * Number of new diagnosed patients
     * @return CohortIndicator
     */
    public CohortIndicator numberOfNewDiagnosedPatients(){
        return cohortIndicator("New diagnosed patients", ReportUtils.map(cohorts.newDiagnosedCases(), "onOrAfter=${startDate},onOrBefore=${endDate}"));
    }

    /**
     * Type of diabetic
     * @return CohortIndicator
     */
    public CohortIndicator numberOfPatientsPerDiabetiType(Concept question, Concept answer) {
       return cohortIndicator("Per diabetic", ReportUtils.map(common.hasObs(question, answer), "onOrAfter=${startDate},onOrBefore=${endDate}"));
    }

    /**
     * The number of GDM
     * @return
     */
    public CohortIndicator numberOfGdm() {
        Concept question = Dictionary.getConcept(Dictionary.TYPE_OF_DIABETIC);
        Concept ans = Dictionary.getConcept(Dictionary.GDM);
        return cohortIndicator("numGdm", ReportUtils.map(common.hasObs(question, ans), "onOrAfter=${startDate},onOrBefore=${endDate}"));
    }

    /**
     * numberOfPatientsOnInsulin()
     */
    public CohortIndicator numberOfPatientsOnInsulin() {
        Concept question = Dictionary.getConcept("f5b23ca2-ee78-4fa8-915d-8c81c8c9d8bd");
        Concept ans = Dictionary.getConcept("1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        return cohortIndicator("onInsulin", ReportUtils.map(common.hasObs(question, ans), "onOrAfter=${startDate},onOrBefore=${endDate}"));
    }

    /**
     * numberOfPatientsOnOglas
     */
    public CohortIndicator numberOfPatientsOnOglas() {

        Concept question = Dictionary.getConcept("2bac6fca-19d0-4e4b-99a7-4bb30f40470b");
        Concept ans = Dictionary.getConcept("1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        return cohortIndicator("onoglas", ReportUtils.map(common.hasObs(question, ans), "onOrAfter=${startDate},onOrBefore=${endDate}"));
    }

    /**
     * numberOfPatientsOnInsAndOgl
     */
    public CohortIndicator numberOfPatientsOnInsAndOgl(){
        Concept question = Dictionary.getConcept("6b90a512-39a4-4eac-a6dc-12b54932f536");
        Concept ans = Dictionary.getConcept("1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        return cohortIndicator("onInsulin+Oglas", ReportUtils.map(common.hasObs(question, ans), "onOrAfter=${startDate},onOrBefore=${endDate}"));

    }
}
