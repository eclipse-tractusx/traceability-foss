import { setupWorker } from 'msw';
import { assetHandlers, coreHandlers } from './services';

const handlers = [...coreHandlers, ...assetHandlers];
export const worker = setupWorker(...handlers);
