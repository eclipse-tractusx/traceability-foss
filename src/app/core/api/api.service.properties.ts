import { environment } from '../../../environments/environment';

export /** @type {*} */
const realm: RegExpExecArray = new RegExp(environment.realmRegExp).exec(window.location.href);
