package org.openmrs.module.aihdreports.fragment.controller;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.aihdreports.reporting.calculation.Calculations;
import org.openmrs.module.aihdreports.reporting.calculation.EmrCalculationUtils;
import org.openmrs.module.aihdreports.reporting.metadata.Dictionary;
import org.openmrs.module.aihdreports.reporting.utils.Filters;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ComplicationsFragmentController {

    public void controller(FragmentModel model,
                           @FragmentParam(value = "requiredLocations", required = false) List<Integer> overral_location,
                           @FragmentParam(value = "allPatients", required = false) List<Patient> allPatients) {

        //get a collection of all patients
        PatientCalculationService patientCalculationService = Context.getService(PatientCalculationService.class);
        PatientCalculationContext context = patientCalculationService.createCalculationContext();
        context.setNow(new Date());
        List<Integer> cohort = new ArrayList<>();
        //loop through all and get their patient ids
        if (allPatients.size() > 0) {
            for (Patient patient : allPatients) {
                cohort.add(patient.getPatientId());
            }
        }
        //exclude dead patients
        Set<Integer> alivePatients = Filters.alive(cohort, context);
        Set<Integer> male = Filters.male(alivePatients, context);
        Set<Integer> female = Filters.female(alivePatients, context);

        //declare concepts here to passed
        Concept problem_added = Dictionary.getConcept(Dictionary.PROBLEM_ADDED);
        Concept stroke = Dictionary.getConcept(Dictionary.STROKE);
        Concept ischemic_heart_disease = Dictionary.getConcept(Dictionary.Ischemic_heart_disease);
        Concept Peripheral_Vascular_disease = Dictionary.getConcept(Dictionary.Peripheral_Vascular_disease);
        Concept Heart_failure = Dictionary.getConcept(Dictionary.Heart_failure);
        Concept Neuropathy = Dictionary.getConcept(Dictionary.Neuropathy);
        Concept Retinopathy = Dictionary.getConcept(Dictionary.Retinopathy);
        Concept Diabetic_foot = Dictionary.getConcept(Dictionary.Diabetic_foot);
        Concept nephropathy = Dictionary.getConcept(Dictionary.Nephropathy);

        //add maps
        model.addAttribute("sM", getCount(problem_added, stroke, male, context, overral_location));
        model.addAttribute("isM", getCount(problem_added, ischemic_heart_disease, male, context, overral_location));
        model.addAttribute("pM", getCount(problem_added, Peripheral_Vascular_disease, male, context, overral_location));
        model.addAttribute("hM", getCount(problem_added, Heart_failure, male, context, overral_location));
        model.addAttribute("nM", getCount(problem_added, Neuropathy, male, context, overral_location));
        model.addAttribute("rM", getCount(problem_added, Retinopathy, male, context, overral_location));
        model.addAttribute("dM", getCount(problem_added, Diabetic_foot, male, context, overral_location));
        model.addAttribute("neM", getCount(problem_added, nephropathy, male, context, overral_location));

        model.addAttribute("sF", getCount(problem_added, stroke, female, context, overral_location));
        model.addAttribute("isF", getCount(problem_added, ischemic_heart_disease, female, context, overral_location));
        model.addAttribute("pF", getCount(problem_added, Peripheral_Vascular_disease, female, context, overral_location));
        model.addAttribute("hF", getCount(problem_added, Heart_failure, female, context, overral_location));
        model.addAttribute("nF", getCount(problem_added, Neuropathy, female, context, overral_location));
        model.addAttribute("rF", getCount(problem_added, Retinopathy, female, context, overral_location));
        model.addAttribute("dF", getCount(problem_added, Diabetic_foot, female, context, overral_location));
        model.addAttribute("neF", getCount(problem_added, nephropathy, female, context, overral_location));

        }

    private Integer getCount(Concept q, Concept a1, Set<Integer> cohort, PatientCalculationContext context, List<Integer> loc){
        Set<Integer> allSet = new HashSet<>();
        CalculationResultMap problem_added = Calculations.lastObs(q, cohort, context);
        for(Integer pId: cohort){
            Obs obs = EmrCalculationUtils.obsResultForPatient(problem_added, pId);
            if(obs != null && (obs.getValueCoded().equals(a1)) && loc.contains(obs.getLocation().getLocationId())){
                allSet.add(pId);
            }
        }
        return allSet.size();
    }
}
