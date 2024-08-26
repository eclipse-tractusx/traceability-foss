
 export enum AdminViewTab {
  BPN_CONFIGURATION_VIEW = 'bpn',
  IMPORT_VIEW = 'import',
  CONTRACT_VIEW = 'contracts'
}

export class AdminPage {
  static visit() {
    cy.get('[data-testid="user-menu"]').click();
    cy.get('[data-testid="user-menu-administration-button"]').click();
  }

  static navigateToTab(tab: AdminViewTab) {
    let sectionTestId: string;
    switch (tab) {
      case AdminViewTab.BPN_CONFIGURATION_VIEW:
        sectionTestId = '/admin/configure-bpn'
        break;
      case AdminViewTab.IMPORT_VIEW:
        sectionTestId = '/admin/configure-import'
        break;
      case AdminViewTab.CONTRACT_VIEW:
        sectionTestId = '/admin/contracts'
        break;
      default: {
        throw new Error("The view tab " + tab +  " does not exist in the administration view. valid views are [bpn, import, contracts]'");
      }
    }
    cy.get(`[data-testid="${sectionTestId}"]`).click();
  }

  static getHeaderOfTabView(tab: AdminViewTab) {
    return cy.get(`[data-testid="admin_${tab}_view_header"`)
  }

  static clickCheckBoxForContractId(contractId: string) {
    return cy.get('td').contains(contractId).parent('tr').find('td mat-checkbox').click();
  }

  static clickCheckBoxForFirstContractInTable() {
    return cy.get('td').first().parent('tr').find('td mat-checkbox').click();
  }

  static getContractIdOfFirstContractInTable() {
    return cy.get('[data-testid="table-component--cell-data"]').first().then(contractId => {
      return contractId.text();
    });
  }


  static clickExportContractsButton() {
    return cy.get('[data-testid="export-contracts-button"]').click();
  }

  static getExportedContractsFileData() {
    return cy.readFile('cypress/downloads/Exported_Contracts.csv', 'utf-8');
  }

}
