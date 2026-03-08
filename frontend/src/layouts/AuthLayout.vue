<template>
  <q-layout view="hHh lpR fFf">
    <q-page-container>
      <q-page class="auth-root">

        <!-- Left panel: hero image + branding -->
        <div class="auth-hero">
          <div class="auth-hero__overlay" />

          <!-- Logo + tagline -->
          <div class="auth-hero__content">
            <div class="auth-hero__logo">IRON LOG</div>
            <div class="auth-hero__tagline">Track. Lift. Progress.</div>
            <div class="auth-hero__bar" />
            <div class="auth-hero__quote">
              "The iron never lies to you."
            </div>
          </div>
        </div>

        <!-- Right panel: form -->
        <div class="auth-form-panel">
          <div class="auth-form-inner">
            <!-- Mobile-only logo -->
            <div class="auth-mobile-logo">IRON LOG</div>

            <router-view />
          </div>
        </div>

      </q-page>
    </q-page-container>
  </q-layout>
</template>

<style scoped>
.auth-root {
  display: flex;
  min-height: 100vh;
  background: #111;
}

/* ── Hero (left) ── */
.auth-hero {
  position: relative;
  flex: 1 1 55%;
  min-height: 300px;
  background-image: url('https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=1400&q=80');
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: flex-end;
}

.auth-hero__overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    135deg,
    rgba(0, 0, 0, 0.82) 0%,
    rgba(13, 13, 13, 0.60) 50%,
    rgba(255, 98, 0, 0.18) 100%
  );
}

.auth-hero__content {
  position: relative;
  z-index: 1;
  padding: 48px;
}

.auth-hero__logo {
  font-size: 3rem;
  font-weight: 900;
  letter-spacing: 0.14em;
  color: #ff6200;
  line-height: 1;
  margin-bottom: 10px;
}

.auth-hero__tagline {
  font-size: 0.85rem;
  letter-spacing: 0.22em;
  text-transform: uppercase;
  color: #aaa;
  margin-bottom: 20px;
}

.auth-hero__bar {
  width: 52px;
  height: 4px;
  background: #ff6200;
  border-radius: 2px;
  margin-bottom: 20px;
}

.auth-hero__quote {
  font-size: 1.05rem;
  font-style: italic;
  color: rgba(255, 255, 255, 0.55);
  max-width: 320px;
  line-height: 1.6;
}

/* ── Form (right) ── */
.auth-form-panel {
  flex: 0 0 400px;
  background: #111;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px 32px;
  border-left: 1px solid #1e1e1e;
}

.auth-form-inner {
  width: 100%;
  max-width: 340px;
}

/* Mobile-only logo */
.auth-mobile-logo {
  display: none;
  font-size: 2rem;
  font-weight: 900;
  letter-spacing: 0.12em;
  color: #ff6200;
  text-align: center;
  margin-bottom: 32px;
}

/* ── Responsive ── */
@media (max-width: 700px) {
  .auth-root { flex-direction: column; }
  .auth-hero {
    flex: 0 0 220px;
    min-height: 220px;
    align-items: center;
    justify-content: center;
  }
  .auth-hero__content { padding: 24px; text-align: center; }
  .auth-hero__bar { margin: 0 auto 20px; }
  .auth-hero__quote { display: none; }
  .auth-form-panel { flex: 1; border-left: none; border-top: 1px solid #1e1e1e; padding: 32px 24px; }
  .auth-mobile-logo { display: block; }
  .auth-hero__logo { display: none; }
  .auth-hero__tagline { display: none; }
}
</style>
