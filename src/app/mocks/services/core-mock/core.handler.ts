import { rest } from 'msw';
import { mockDashboard } from './core.model';

export const coreHandlers = [
  rest.get('/get-mspids', (req, res, ctx) => {
    return res(ctx.status(200), ctx.json({ data: ['MOCK'] }));
  }),

  rest.get('/organisations', (req, res, ctx) => {
    return res(ctx.status(200), ctx.json({ data: [''] }));
  }),

  rest.post('/kpi-stats', (req, res, ctx) => {
    return res(ctx.status(200), ctx.json({ data: mockDashboard }));
  }),

  rest.get('/tiles', (req, res, ctx) => {
    return res(ctx.status(200), ctx.json({ data: '' }));
  }),
];
