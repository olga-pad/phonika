const { test, expect } = require('@playwright/test');

const mastered = chars => Object.fromEntries(chars.map(ch => [ch, { self: 3, mastered: true, masteredAt: 1, dueRound: 0, lastSeenRound: 0 }]));

async function resetWith(page, chars, level = 2, wordSessionSize = 5) {
  await page.goto('./');
  await page.evaluate(({ chars, level, wordSessionSize }) => {
    localStorage.clear();
    localStorage.setItem('soundsteps-profile-v1', JSON.stringify({
      version: 3,
      profile: { id: 'games-learning-test', name: '' },
      level,
      style: 'upper',
      section: 'words',
      sounds: Object.fromEntries(chars.map(ch => [ch, { self: 3, mastered: true, masteredAt: 1, dueRound: 0, lastSeenRound: 0 }])),
      words: {},
      soundQueue: [],
      wordQueue: [],
      soundRound: 1,
      wordSessionSize,
      letterSessionSize: 5
    }));
  }, { chars, level, wordSessionSize });
  await page.reload();
}

async function available(page) {
  return page.evaluate(() => availableWords().map(([word]) => word));
}

async function openGames(page) {
  await page.locator('#gamesTab').click();
}

test('Games word sources use availableWords and not sessionWords or unrestricted curriculum fallback', async ({ page }) => {
  await page.goto('./');
  for (const file of ['find.js', 'catch.js', 'build-word.js', 'missing-word.js']) {
    const source = await page.evaluate(async name => (await fetch(`./games/${name}`)).text(), file);
    expect(source).toContain('availableWords()');
    expect(source).not.toContain('sessionWords');
  }
  for (const file of ['find.js', 'catch.js']) {
    const source = await page.evaluate(async name => (await fetch(`./games/${name}`)).text(), file);
    expect(source).not.toContain('Object.values(curriculum).flat()');
  }
});

test('Find targets and distractors stay inside availableWords and session size limits targets', async ({ page }) => {
  await resetWith(page, ['а','о','м','с','к','т','и','н','у'], 2, 3);
  const allowed = new Set(await available(page));
  expect(allowed.size).toBeGreaterThan(3);
  await openGames(page);
  await page.locator('#findGameCard').click();
  await page.locator('#findWordsMode').click();
  await expect(page.locator('#findDinoSteps .dino-step')).toHaveCount(6);
  const labels = await page.locator('#findAnswers .find-answer').allTextContents();
  expect(labels.length).toBeGreaterThan(0);
  for (const label of labels) expect(allowed.has(label.toLowerCase())).toBeTruthy();
});

test('Catch target and distractor words stay inside availableWords', async ({ page }) => {
  await resetWith(page, ['а','о','м','с','к','т','и','н','у'], 2, 5);
  const allowed = new Set(await available(page));
  await openGames(page);
  await page.locator('#catchGameCard').click();
  const labels = await page.locator('#catchArea .catch-answer').allTextContents();
  expect(labels.length).toBeGreaterThan(0);
  for (const label of labels) expect(allowed.has(label.toLowerCase())).toBeTruthy();
});

test('Build Word target is composed only from an available word', async ({ page }) => {
  await resetWith(page, ['а','о','м','с','к','т','и','н','у'], 2, 5);
  const allowed = await available(page);
  await openGames(page);
  await page.locator('#buildWordGameCard').click();
  const letters = (await page.locator('#buildLetters .build-letter').allTextContents()).map(x => x.toLowerCase()).sort().join('');
  const signatures = allowed.map(word => [...word].sort().join(''));
  expect(signatures).toContain(letters);
});

test('Missing Word displays only words from availableWords', async ({ page }) => {
  await resetWith(page, ['а','о','м','с','к','т','и','н','у'], 2, 5);
  const allowed = new Set(await available(page));
  await openGames(page);
  await page.locator('#missingWordGameCard').click();
  const shown = await page.locator('#missingFirst, #missingSecond').allTextContents();
  for (const label of shown) expect(allowed.has(label.toLowerCase())).toBeTruthy();
});

test('unmastered letters cannot enter Games words', async ({ page }) => {
  await resetWith(page, ['а','о','м','с','к','т'], 2, 10);
  const allowed = await available(page);
  expect(allowed).not.toContain('кит');
  expect(allowed).not.toContain('нос');
  expect(allowed).not.toContain('сон');
  await openGames(page);
  await page.locator('#findGameCard').click();
  await page.locator('#findWordsMode').click();
  const labels = (await page.locator('#findAnswers .find-answer').allTextContents()).map(x => x.toLowerCase());
  expect(labels).not.toContain('кит');
  expect(labels).not.toContain('нос');
  expect(labels).not.toContain('сон');
});

test('adding a mastered letter makes newly eligible words available to a new game session', async ({ page }) => {
  await resetWith(page, ['о','с'], 2, 10);
  expect(await available(page)).toEqual([]);
  await page.locator('#parentOpen').click();
  await page.locator('.tone-known .collapse-toggle').click();
  await page.locator('#knownSounds .known-letter[data-sound="н"]').click();
  await page.locator('#parentBack').click();
  const allowed = await available(page);
  expect(allowed).toEqual(expect.arrayContaining(['нос','сон']));
  await openGames(page);
  await page.locator('#findGameCard').click();
  await page.locator('#findWordsMode').click();
  const labels = (await page.locator('#findAnswers .find-answer').allTextContents()).map(x => x.toLowerCase());
  expect(labels.some(word => word === 'нос' || word === 'сон')).toBeTruthy();
});

test('creating Games sessions does not change Reading queue or sessionWords', async ({ page }) => {
  await resetWith(page, ['а','о','м','с','к','т','и','н','у'], 2, 5);
  const before = await page.evaluate(() => ({ sessionWords: [...sessionWords], wordQueueState: [...wordQueueState] }));
  await openGames(page);
  await page.locator('#findGameCard').click();
  await page.locator('#findWordsMode').click();
  const after = await page.evaluate(() => ({ sessionWords: [...sessionWords], wordQueueState: [...wordQueueState] }));
  expect(after).toEqual(before);
});

test('small availableWords pool is reused without unknown curriculum fallback', async ({ page }) => {
  await resetWith(page, ['к','и','т'], 2, 5);
  const allowed = await available(page);
  expect(allowed).toEqual(['кит']);
  await openGames(page);
  await page.locator('#missingWordGameCard').click();
  await expect(page.locator('#missingFirst')).toHaveText('КИТ');
  await expect(page.locator('#missingSecond')).toHaveText('КИТ');
});
