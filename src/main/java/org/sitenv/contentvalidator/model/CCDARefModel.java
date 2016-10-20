package org.sitenv.contentvalidator.model;

import org.apache.log4j.Logger;
import org.sitenv.contentvalidator.dto.ContentValidationResult;
import org.sitenv.contentvalidator.dto.enums.ContentValidationResultLevel;

import java.util.ArrayList;

public class CCDARefModel {
	
	private static Logger log = Logger.getLogger(CCDARefModel.class.getName());
	
	private CCDAPatient        patient;
	private CCDACareTeamMember members;
	private CCDAEncounter      encounter;
	private CCDAAllergy        allergy;
	private CCDAMedication     medication;
	private CCDAImmunization   immunization;
	private CCDALabResult      labResults;
	private CCDALabResult  	   labTests;
	private CCDAProcedure      procedure;
	private CCDASocialHistory  smokingStatus;
	private CCDAVitalSigns     vitalSigns;
	private CCDAProblem        problem;
	private CCDAPlanOfTreatment planOfTreatment;
	private CCDAGoals          goals;
	private CCDAHealthConcerns hcs;
	private ArrayList<CCDAUDI> udi;
	
	public CCDARefModel() {
		udi = new ArrayList<CCDAUDI>();
	}
	
	public ArrayList<ContentValidationResult> compare(String validationObjective, CCDARefModel submittedCCDA) {
		
		ArrayList<ContentValidationResult> results = new ArrayList<ContentValidationResult>();
		
		if(doesObjectiveRequireCCDS(validationObjective))
		{
			log.info(" Performing CCDS checks ");
			compareCCDS(validationObjective, submittedCCDA, results);
		}
		else 
		{
			log.info(" Not performing CCDS checks ");
		}
		
		return results;
	}
	
	public void compareCCDS(String validationObjective, CCDARefModel submittedCCDA,ArrayList<ContentValidationResult> results) 
	{
		log.info("Comparing Patient Data ");
		comparePatients(submittedCCDA, results);
		
		log.info("Validating Birth Sex ");
		validateBirthSex(submittedCCDA, results);
		
		log.info("Comparing Problems ");
		compareProblems(validationObjective, submittedCCDA, results);
		
		log.info("Comparing Allergies ");
		compareAllergies(validationObjective, submittedCCDA, results);
		
		log.info("Finished comparison , returning results");
		
	}
	
	private void compareProblems(String validationObjective, CCDARefModel submittedCCDA, ArrayList<ContentValidationResult> results) {
		
		if((this.getProblem() != null) && (submittedCCDA.getProblem() != null) ) {
			log.info("Start Problem Comparison ");
			this.problem.compare(submittedCCDA.getProblem(), results);
		}
		else if ( (this.getProblem() != null) && (submittedCCDA.getProblem() == null) ) 
		{
			// handle the case where the problem section does not exist in the submitted CCDA
			ContentValidationResult rs = new ContentValidationResult("The scenario requires data related to patient's problems, but the submitted C-CDA does not contain problem data.", ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0" );
			results.add(rs);
			log.info(" Scenario requires problems but submitted document does not contain problems section");
		}
		else if ( (this.getProblem() == null) && (submittedCCDA.getProblem() != null) ){
			
			ContentValidationResult rs = new ContentValidationResult("The scenario does not require data related to patient's problems, but the submitted C-CDA does contain problem data.", ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0" );
			results.add(rs);
			log.info("Model does not have problems for comparison ");
		}
		else {
			
			log.info("Model and Submitted CCDA do not have problems for comparison ");
		}
		
	}
	
	private void compareAllergies(String validationObjective, CCDARefModel submittedCCDA, ArrayList<ContentValidationResult> results) {
		
		if((this.getAllergy() != null) && (submittedCCDA.getAllergy() != null) ) {
			log.info("Start Allergy Comparison ");
			this.allergy.compare(submittedCCDA.getAllergy(), results);
		}
		else if ( (this.getAllergy() != null) && (submittedCCDA.getAllergy() == null) ) 
		{
			// handle the case where the allergy section does not exist in the submitted CCDA
			ContentValidationResult rs = new ContentValidationResult("The scenario requires data related to patient's allergies, but the submitted C-CDA does not contain allergy data.", ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0" );
			results.add(rs);
			log.info(" Scenario requires allergies but submitted document does not contain allergies section");
		}
		else if ( (this.getAllergy() == null) && (submittedCCDA.getAllergy() != null) ){
			
			ContentValidationResult rs = new ContentValidationResult("The scenario does not require data related to patient's allergies, but the submitted C-CDA does contain allergy data.", ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0" );
			results.add(rs);
			log.info("Model does not have allergies for comparison ");
		}
		else {
			
			log.info("Model and Submitted CCDA do not have problems for comparison ");
		}
		
	}
	
	private void validateBirthSex(CCDARefModel submittedCCDA, ArrayList<ContentValidationResult> results) {
		
		if( (submittedCCDA.getSmokingStatus() != null) &&
			 (submittedCCDA.getSmokingStatus().getBirthSex() != null)) {
			
			// Validate that the code is M or F.
			if( (submittedCCDA.getSmokingStatus().getBirthSex().getSexCode() != null)  && 
				((submittedCCDA.getSmokingStatus().getBirthSex().getSexCode().getCode().equalsIgnoreCase("M")) || 
				(submittedCCDA.getSmokingStatus().getBirthSex().getSexCode().getCode().equalsIgnoreCase("F"))) )
			{
				//do nothing.
				return;
			}
			else {
				ContentValidationResult rs = new ContentValidationResult("The scenario requires patient's birth sex to use the codes M or F but the submitted C-CDA does not contain either of these codes.", ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0" );
				results.add(rs);
			}
			
		}
		else {
			ContentValidationResult rs = new ContentValidationResult("The scenario requires patient's birth sex to be captured as part of social history data, but submitted file does have birth sex information", ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0" );
			results.add(rs);
		}
	}
	
	
	private void comparePatients(CCDARefModel submittedCCDA, ArrayList<ContentValidationResult> results) {
		
		if((patient != null) && (submittedCCDA.getPatient() != null)) {
			this.patient.compare(submittedCCDA.getPatient(), results);
		}
		else if( (patient == null) && (submittedCCDA.getPatient() != null) ) {
			ContentValidationResult rs = new ContentValidationResult("The scenario does not require patient data, but submitted file does have patient data", ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0" );
			results.add(rs);
		}
		else if((patient != null) && (submittedCCDA.getPatient() == null)){
			ContentValidationResult rs = new ContentValidationResult("The scenario requires patient data, but submitted file does have patient data", ContentValidationResultLevel.ERROR, "/ClinicalDocument", "0" );
			results.add(rs);
		}
		else {
			log.info("Both the Ref Model and the Submitted Patient Data are null ");
		}
	}

	public Boolean doesObjectiveRequireCCDS(String valObj) {
		
		if(valObj.equalsIgnoreCase("170.315_b1_ToC_Amb") || 
			valObj.equalsIgnoreCase("170.315_b1_ToC_Inp") ||
			valObj.equalsIgnoreCase("170.315_b4_CCDS_Amb") ||
			valObj.equalsIgnoreCase("170.315_b4_CCDS_Inp") ||
			valObj.equalsIgnoreCase("170.315_b6_DE_Amb") ||
			valObj.equalsIgnoreCase("170.315_b6_DE_Inp") ||
			valObj.equalsIgnoreCase("170.315_e1_VDT_Amb") ||
			valObj.equalsIgnoreCase("170.315_e1_VDT_Inp") ||
			valObj.equalsIgnoreCase("170.315_g9_APIAccess_Amb") || 
			valObj.equalsIgnoreCase("170.315_g9_APIAccess_Inp") )
				return true;
		else
			return false;
	}
	
	
	public void log() {
		
		if(patient != null)
			patient.log();
		else
			log.info(" No Patient Data in the model ");
		
		if(encounter != null)
			encounter.log();
		else
			log.info("No Encounter data in the model");
		
		if(problem != null)
			problem.log();
		else
			log.info("No Problem data in the model");
		
		if(medication != null)
			medication.log();
		else
			log.info("No Medication data in the model");
		
		if(allergy != null)
			allergy.log();
		else
			log.info("No Allergy data in the model");
		
		if(immunization != null)
			immunization.log();
		else
			log.info("No Immunization data in the model");
		
		if(labResults != null)
			labResults.log();
		else
			log.info("No Lab Results data in the model");
		
		if(labTests != null)
			labTests.log();
		else
			log.info("No Lab Tests data in the model");
		
		if(procedure != null)
			procedure.log();
		else
			log.info("No Procedure data in the model");
		
		if(smokingStatus != null)
			smokingStatus.log();
		else
			log.info("No Smoking Status in the model");
		
		if(vitalSigns != null)
			vitalSigns.log();
		else
			log.info("No Vital Signs data in the model");
		
		if(goals != null)
			goals.log();
		else
			log.info("No Goals data in the model");
		
		if(planOfTreatment != null)
			planOfTreatment.log();
		else
			log.info("No Plan of Treatment data in the model");
		
		
		if(hcs != null)
			hcs.log();
		else
			log.info("No Health Concerns data in the model");
		
		if(members != null)
			members.log();
		else
			log.info("No Care Team Members data in the model");
		
		for(int j = 0; j < udi.size(); j++) {
			udi.get(j).log();
			
		}
	}
	
	
	public CCDAPatient getPatient() {
		return patient;
	}
	public void setPatient(CCDAPatient patient) {
		this.patient = patient;
	}
	public CCDACareTeamMember getMembers() {
		return members;
	}
	public void setMembers(CCDACareTeamMember members) {
		this.members = members;
	}
	public CCDAEncounter getEncounter() {
		return encounter;
	}
	public void setEncounter(CCDAEncounter encounter) {
		this.encounter = encounter;
	}
	public CCDAAllergy getAllergy() {
		return allergy;
	}
	public void setAllergy(CCDAAllergy allergy) {
		this.allergy = allergy;
	}
	public CCDAMedication getMedication() {
		return medication;
	}
	public void setMedication(CCDAMedication medication) {
		this.medication = medication;
	}
	public CCDAImmunization getImmunization() {
		return immunization;
	}
	public void setImmunization(CCDAImmunization immunization) {
		this.immunization = immunization;
	}
	public CCDALabResult getLabResults() {
		return labResults;
	}
	public void setLabResults(CCDALabResult labResults) {
		this.labResults = labResults;
	}
	public CCDALabResult getLabTests() {
		return labTests;
	}
	public void setLabTests(CCDALabResult labTests) {
		this.labTests = labTests;
	}
	public CCDAProcedure getProcedure() {
		return procedure;
	}
	public void setProcedure(CCDAProcedure procedure) {
		this.procedure = procedure;
	}
	public CCDASocialHistory getSmokingStatus() {
		return smokingStatus;
	}
	public void setSmokingStatus(CCDASocialHistory smokingStatus) {
		this.smokingStatus = smokingStatus;
	}
	public CCDAVitalSigns getVitalSigns() {
		return vitalSigns;
	}
	public void setVitalSigns(CCDAVitalSigns vitalSigns) {
		this.vitalSigns = vitalSigns;
	}
	public CCDAProblem getProblem() {
		return problem;
	}
	public void setProblem(CCDAProblem problem) {
		this.problem = problem;
	}
	public CCDAPlanOfTreatment getPlanOfTreatment() {
		return planOfTreatment;
	}
	public void setPlanOfTreatment(CCDAPlanOfTreatment planOfTreatment) {
		this.planOfTreatment = planOfTreatment;
	}
	public CCDAGoals getGoals() {
		return goals;
	}
	public void setGoals(CCDAGoals goals) {
		this.goals = goals;
	}
	public CCDAHealthConcerns getHcs() {
		return hcs;
	}
	public void setHcs(CCDAHealthConcerns hcs) {
		this.hcs = hcs;
	}
	public ArrayList<CCDAUDI> getUdi() {
		return udi;
	}
	public void setUdi(ArrayList<CCDAUDI> udis) {
		
		if(udis != null)
			this.udi = udis;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((allergy == null) ? 0 : allergy.hashCode());
		result = prime * result
				+ ((encounter == null) ? 0 : encounter.hashCode());
		result = prime * result + ((goals == null) ? 0 : goals.hashCode());
		result = prime * result + ((hcs == null) ? 0 : hcs.hashCode());
		result = prime * result
				+ ((immunization == null) ? 0 : immunization.hashCode());
		result = prime * result
				+ ((labResults == null) ? 0 : labResults.hashCode());
		result = prime * result
				+ ((labTests == null) ? 0 : labTests.hashCode());
		result = prime * result
				+ ((medication == null) ? 0 : medication.hashCode());
		result = prime * result + ((members == null) ? 0 : members.hashCode());
		result = prime * result + ((patient == null) ? 0 : patient.hashCode());
		result = prime * result
				+ ((planOfTreatment == null) ? 0 : planOfTreatment.hashCode());
		result = prime * result + ((problem == null) ? 0 : problem.hashCode());
		result = prime * result
				+ ((procedure == null) ? 0 : procedure.hashCode());
		result = prime * result
				+ ((smokingStatus == null) ? 0 : smokingStatus.hashCode());
		result = prime * result + ((udi == null) ? 0 : udi.hashCode());
		result = prime * result
				+ ((vitalSigns == null) ? 0 : vitalSigns.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CCDARefModel other = (CCDARefModel) obj;
		if (allergy == null) {
			if (other.allergy != null)
				return false;
		} else if (!allergy.equals(other.allergy))
			return false;
		if (encounter == null) {
			if (other.encounter != null)
				return false;
		} else if (!encounter.equals(other.encounter))
			return false;
		if (goals == null) {
			if (other.goals != null)
				return false;
		} else if (!goals.equals(other.goals))
			return false;
		if (hcs == null) {
			if (other.hcs != null)
				return false;
		} else if (!hcs.equals(other.hcs))
			return false;
		if (immunization == null) {
			if (other.immunization != null)
				return false;
		} else if (!immunization.equals(other.immunization))
			return false;
		if (labResults == null) {
			if (other.labResults != null)
				return false;
		} else if (!labResults.equals(other.labResults))
			return false;
		if (labTests == null) {
			if (other.labTests != null)
				return false;
		} else if (!labTests.equals(other.labTests))
			return false;
		if (medication == null) {
			if (other.medication != null)
				return false;
		} else if (!medication.equals(other.medication))
			return false;
		if (members == null) {
			if (other.members != null)
				return false;
		} else if (!members.equals(other.members))
			return false;
		if (patient == null) {
			if (other.patient != null)
				return false;
		} else if (!patient.equals(other.patient))
			return false;
		if (planOfTreatment == null) {
			if (other.planOfTreatment != null)
				return false;
		} else if (!planOfTreatment.equals(other.planOfTreatment))
			return false;
		if (problem == null) {
			if (other.problem != null)
				return false;
		} else if (!problem.equals(other.problem))
			return false;
		if (procedure == null) {
			if (other.procedure != null)
				return false;
		} else if (!procedure.equals(other.procedure))
			return false;
		if (smokingStatus == null) {
			if (other.smokingStatus != null)
				return false;
		} else if (!smokingStatus.equals(other.smokingStatus))
			return false;
		if (udi == null) {
			if (other.udi != null)
				return false;
		} else if (!udi.equals(other.udi))
			return false;
		if (vitalSigns == null) {
			if (other.vitalSigns != null)
				return false;
		} else if (!vitalSigns.equals(other.vitalSigns))
			return false;
		return true;
	}

		

}
