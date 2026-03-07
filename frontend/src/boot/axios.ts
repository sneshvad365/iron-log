import { boot } from 'quasar/wrappers'

// Axios client is configured in api/client.ts using the auth store.
// No global setup needed here — the interceptor is applied on import.
export default boot(() => {})
