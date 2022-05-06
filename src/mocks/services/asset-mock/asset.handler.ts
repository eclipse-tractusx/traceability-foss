import { rest } from 'msw';
import { mockAssets } from './assets.model';

export const assetHandlers = [
  rest.get('/get-asset-detail', (req, res, ctx) => {
    const serialNumberCustomer = req.url.searchParams.get('serialNumberCustomer');

    return res(ctx.status(200), ctx.json(mockAssets[serialNumberCustomer]));
  }),

  rest.get('/get-asset-parent', (req, res, ctx) => {
    const serialNumberCustomer = req.url.searchParams.get('serialNumberCustomer');

    return res(ctx.status(200), ctx.json(mockAssets[serialNumberCustomer]));
  }),
];
