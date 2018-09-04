package org.openmrs.module.aihdreports.fragment.controller;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.User;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.aihdreports.reporting.calculation.Calculations;
import org.openmrs.module.aihdreports.reporting.calculation.EmrCalculationUtils;
import org.openmrs.module.aihdreports.reporting.metadata.Dictionary;
import org.openmrs.module.aihdreports.reporting.utils.Filters;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BpSummaryFragmentController {

    public void controller(FragmentModel model,
                           @SpringBean("locationService") LocationService locationService) {
        //set context
        PatientCalculationService patientCalculationService = Context.getService(PatientCalculationService.class);
        PatientCalculationContext context = patientCalculationService.createCalculationContext();
        context.setNow(new Date());
        //get a collection of all patients
        List<Patient> allPatients = Context.getPatientService().getAllPatients();
        List<Integer> cohort = new ArrayList<>();
        //loop through all and get their patient ids
        if(allPatients.size() > 0) {
            for (Patient patient : allPatients) {
                cohort.add(patient.getPatientId());
            }
        }
        User user = Context.getAuthenticatedUser();
        PersonAttribute attribute = user.getPerson().getAttribute(Context.getPersonService().getPersonAttributeTypeByUuid("8930b69a-8e7c-11e8-9599-337483600ed7"));
        Location loggedInLocation = locationService.getLocation(1);
        if(attribute != null && StringUtils.isNotEmpty(attribute.getValue())){
            loggedInLocation = locationService.getLocation(Integer.parseInt(attribute.getValue()));
        }
        //exclude dead patients
        Set<Integer> alivePatients = Filters.alive(cohort, context);
        Set<Integer> male = Filters.male(alivePatients, context);
        Set<Integer> female = Filters.female(alivePatients, context);
        //declare concepts
        Concept systtollic = Dictionary.getConcept(Dictionary.SYSTOLIC_BLOOD_PRESSURE);
        Concept diastollic = Dictionary.getConcept(Dictionary.DIASTOLIC_BLOOD_PRESSURE);

        model.addAttribute("above14090M", getPressure(systtollic, diastollic, male, context, 140, 90, loggedInLocation));
        model.addAttribute("above14090F", getPressure(systtollic, diastollic, female, context, 140, 90, loggedInLocation));
    }

    private Integer getPressure(Concept q1, Concept q2, Set<Integer> cohort, PatientCalculationContext context, double systollic, double diastollic, Location location){
        Set<Integer> allSet = new HashSet<>();
        CalculationResultMap systo = Calculations.lastObs(q1, cohort, context);
        CalculationResultMap diasto = Calculations.lastObs(q2, cohort, context);

        for(Integer pId: cohort){
            Double sys = EmrCalculationUtils.numericObsResultForPatient(systo, pId);
            Double dia = EmrCalculationUtils.numericObsResultForPatient(diasto, pId);
            Obs wObs = EmrCalculationUtils.obsResultForPatient(systo, pId);
            if(sys != null && dia !=null && wObs != null && wObs.getLocation().equals(location)){
                if(sys > systollic && dia > 90) {
                    allSet.add(pId);
                }
            }

        }
        return allSet.size();
    }
}
