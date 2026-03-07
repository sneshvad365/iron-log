<template>
  <q-layout view="hHh lpR fFf">
    <q-page-container>
      <q-page class="flex flex-center auth-bg">
        <div style="width: 100%; max-width: 420px">
          <div class="text-center q-mb-xl">
            <div style="font-size: 2.4rem; font-weight: 900; letter-spacing: 0.12em; color: #ff6200;">
              IRON LOG
            </div>
            <div style="font-size: 0.8rem; letter-spacing: 0.2em; color: #666; text-transform: uppercase; margin-top: 4px;">
              Track. Lift. Progress.
            </div>
          </div>
          <router-view />
        </div>
      </q-page>
    </q-page-container>
  </q-layout>
</template>
