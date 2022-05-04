import { Component, HostListener } from '@angular/core';
import { realm } from 'src/app/core/api/api.service.properties';
import { Router } from '@angular/router';
import { LayoutFacade } from 'src/app/shared/abstraction/layout-facade';
import { Mspid } from 'src/app/shared/model/mspid.model';

/**
 *
 *
 * @export
 * @class NavBarComponent
 */
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
    this.layoutFacade.mspids.subscribe((realms: Mspid[]) => {
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
    this.router.navigate([`/${realm[1]}`]).then();
  }

  public getCompanyLogo(): string {
    const logo = {
      bmw: '/assets/images/BMW_2.png',
      'taas-zf': '/assets/images/zf.png',
      'taas-gris': '/assets/images/zf.png',
      'taas-henkel': '/assets/images/henkel-logo-0.png',
      'taas-basf': '/assets/images/basf.png',
    };
    return logo[realm[1]];
  }

  @HostListener('window:click', [])
  private onClick(): void {
    this.isExpanded = false;
  }
}
