import { environment } from '../../../../environments/environment';

export /** @type {*} */
const realm: string = new RegExp(environment.realmRegExp).exec(window.location.href)?.[1];
