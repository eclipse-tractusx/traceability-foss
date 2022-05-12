import { Component, HostListener } from '@angular/core';
import { Router } from '@angular/router';
import { realm, realmLogo } from 'src/app/modules/core/api/api.service.properties';
import { LayoutFacade } from 'src/app/modules/shared/abstraction/layout-facade';
import { Mspid } from 'src/app/modules/shared/model/mspid.model';

@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrls: ['./nav-bar.component.scss'],
})
export class NavBarComponent {
  public isExpanded = false;
  public userInitials = '';
  public userDetails = { name: '', email: '', role: '' };
  public realm: Mspid;
  public isIconLoading = false;

  constructor(private layoutFacade: LayoutFacade, private router: Router) {
    this.userInitials = this.layoutFacade.realmName;
    this.userDetails = this.layoutFacade.getUserInformation;
    this.layoutFacade.mspids$.subscribe((realms: Mspid[]) => {
      this.realm = realms.find(mspid => mspid.name === this.layoutFacade.mspid);
      if (this.realm) {
        this.realm.name =
          this.layoutFacade.mspid === this.layoutFacade.realmName
            ? this.layoutFacade.mspid
            : this.layoutFacade.realmName;
        this.isIconLoading = false;
      }
    });
  }

  ngOnInit(): void {
    this.isIconLoading = true;
    this.userDetails = this.layoutFacade.getUserInformation;
    this.layoutFacade.setMspids();
  }

  public expand(event: Event): void {
    if (event) {
      event.stopPropagation();
      this.isExpanded = !this.isExpanded;
    }
  }

  public logOut(): void {
    this.layoutFacade.logOut();
  }

  public navigateToHome(): void {
    this.router.navigate([`/${realm}`]).then();
  }

  public getCompanyLogo(): string {
    return realmLogo;
  }

  @HostListener('window:click', [])
  private onClick(): void {
    this.isExpanded = false;
  }
}
