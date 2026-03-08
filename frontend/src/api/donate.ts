import client from './client'

export const createDonationSession = (amount: number) =>
  client.post('/api/donate/session', { amount })
