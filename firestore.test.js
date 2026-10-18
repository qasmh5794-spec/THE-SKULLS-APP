const { test, before, after } = require('node:test');
const assert = require('node:assert');
const fs = require('node:fs');
const { initializeTestEnvironment, assertFails, assertSucceeds } = require('@firebase/rules-unit-testing');

const DATABASE_ID = 'ai-studio-android-theskull-e4b5f845-3f57-4b8d-80a9-a6409c12387b';
let testEnv;

before(async () => {
  const rules = fs.readFileSync('firestore.rules', 'utf8');
  testEnv = await initializeTestEnvironment({
    projectId: 'demo-no-project',
    firestore: {
      rules,
      host: '127.0.0.1',
      port: 8085
    }
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

test('Unauthenticated user cannot read or write user profiles', async () => {
  const unauthedDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthedDb.collection('users').doc('user1').get());
  await assertFails(unauthedDb.collection('users').doc('user1').set({
    userId: 'user1',
    displayName: 'Player One'
  }));
});

test('Authenticated user can create their own profile', async () => {
  const aliceDb = testEnv.authenticatedContext('alice').firestore();
  await assertSucceeds(aliceDb.collection('users').doc('alice').set({
    userId: 'alice',
    displayName: 'Alice Gamer',
    gamerTag: 'AliceSkull',
    favoritePlatform: 'PS5'
  }));
});

test('Authenticated user cannot create profile for another user', async () => {
  const aliceDb = testEnv.authenticatedContext('alice').firestore();
  await assertFails(aliceDb.collection('users').doc('bob').set({
    userId: 'bob',
    displayName: 'Bob Gamer'
  }));
});

test('Authenticated user can like a review', async () => {
  const aliceDb = testEnv.authenticatedContext('alice').firestore();
  await assertSucceeds(aliceDb.collection('reviews').doc('elden-ring-shadow').collection('likes').doc('alice').set({
    userId: 'alice',
    reviewId: 'elden-ring-shadow'
  }));
});

test('User cannot like on behalf of another user', async () => {
  const aliceDb = testEnv.authenticatedContext('alice').firestore();
  await assertFails(aliceDb.collection('reviews').doc('elden-ring-shadow').collection('likes').doc('bob').set({
    userId: 'bob',
    reviewId: 'elden-ring-shadow'
  }));
});

test('Authenticated user can create a comment with valid fields', async () => {
  const aliceDb = testEnv.authenticatedContext('alice').firestore();
  await assertSucceeds(aliceDb.collection('reviews').doc('elden-ring-shadow').collection('comments').doc('comment1').set({
    id: 'comment1',
    userId: 'alice',
    userName: 'Alice',
    reviewId: 'elden-ring-shadow',
    comment: 'أفضل لعبة لعبتها في حياتي! تصميم الزعماء لا يصدق.',
    rating: 10
  }));
});

test('Authenticated user cannot spoof comment userId', async () => {
  const aliceDb = testEnv.authenticatedContext('alice').firestore();
  await assertFails(aliceDb.collection('reviews').doc('elden-ring-shadow').collection('comments').doc('comment2').set({
    id: 'comment2',
    userId: 'bob',
    userName: 'Bob',
    reviewId: 'elden-ring-shadow',
    comment: 'تعليق مزور'
  }));
});
