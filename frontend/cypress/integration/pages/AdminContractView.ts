import { Then, When } from '@badeball/cypress-cucumber-preprocessor';


export class ContractAdminView {

}

let currentContractId: string;

When("navigate to view section {string} in administration view", (viewSection: string) => {
  let sectionTestId: string;
  switch (viewSection) {
    case 'bpn':
      sectionTestId = '/admin/configure-bpn'
      break;
    case 'import':
      sectionTestId = '/admin/configure-import'
      break;
    case 'contracts':
      sectionTestId = '/admin/contracts'
      break;
    default: {
      throw new Error("The view section " + viewSection +  " does not exist in the administration view. valid views are [bpn, import, contracts]'");
    }
  }
  cy.get(`[data-testid="${sectionTestId}"]`).click();
})

When("select contract with contract-id {string}", function(contractId: string) {
    currentContractId = contractId;
    cy.get('td').contains(currentContractId).parent('tr').find('td mat-checkbox').click();
});

When("export selected contracts", function() {
    cy.get('[data-testid="export-contracts-button"]').click();
});

Then("exported contracts csv file is existing", function() {
    cy.readFile('cypress/downloads/Exported_Contracts.csv', 'utf-8').then((data) => {
        expect(data).to.not.be.null
    })

})

Then("export csv file has correct content", function() {
    cy.readFile('cypress/downloads/Exported_Contracts.csv', 'utf-8').then((data) => {
        expect(data).to.equal(`contractId,counterpartyAddress,creationDate,endDate,state\n${currentContractId},https://trace-x-edc-e2e-a.dev.demo.catena-x.net/api/v1/dsp,Thu Mar 07 2024,Thu Jan 01 1970,FINALIZED`)
    })

})
