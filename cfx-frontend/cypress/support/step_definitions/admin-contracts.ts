import { Then, When } from '@badeball/cypress-cucumber-preprocessor';
import { AdminPage, AdminViewTab } from '../../integration/pages/AdminPage';

let currentContractId: string;

When('navigate to administration view tab {string}', (tabName: AdminViewTab) => {
  AdminPage.navigateToTab(tabName);
  const header = AdminPage.getHeaderOfTabView(tabName);
  switch (tabName) {
    case AdminViewTab.BPN_CONFIGURATION_VIEW:
      header.contains('BPN - EDC Konfiguration').should('be.visible') || header.contains('BPN - EDC configuration').should('be.visible');
      break;
    case AdminViewTab.IMPORT_VIEW:
      header.contains('Trace-X Datenimport').should('be.visible') || header.contains('Trace-X Data import').should('be.visible');
      break;
    case AdminViewTab.CONTRACT_VIEW:
      header.contains('Verträge').should('be.visible') || header.contains('Contracts').should('be.visible');
      break;
    default: {
      throw new Error(`The View Tab header ${ tabName } did not load or is not existing`);
    }
  }
});

When('select contract with contract-id {string}', function(contractId: string) {
  currentContractId = contractId;
  AdminPage.clickCheckBoxForContractId(contractId).should('have.class', 'mat-mdc-checkbox-checked');
});

When('select the first contract in the contracts table', function() {
   AdminPage.getContractIdOfFirstContractInTable().then(contractId => {
     currentContractId = contractId;
     expect(currentContractId).not.to.be.null
   });
  cy.wait(1000);

  AdminPage.clickCheckBoxForFirstContractInTable().should('have.class', 'mat-mdc-checkbox-checked');
})

When('export selected contracts', function() {
  AdminPage.clickExportContractsButton().should('be.visible');
});

Then('exported contracts csv file is existing', function() {
  AdminPage.getExportedContractsFileData().then((data) => {
    expect(data).to.not.be.null;
  });

});

Then('exported csv file has correct content', function() {
  AdminPage.getExportedContractsFileData().then((data) => {
    let expectedData = currentContractId.trim().replace(/\n/g,'');
    expect(data).to.contain(expectedData);
  });

});
