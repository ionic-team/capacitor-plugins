import { glob } from 'glob';
import { promises as fs } from 'fs';

// Define the search pattern
const searchPattern = '**/android/settings.gradle';

// Define the string to search for and the string to replace it with
const searchString = `new File('../node_modules/@capacitor/android/capacitor')`;
const replacementString = `new File('../../node_modules/@capacitor/android/capacitor')`;

(async () => {
  try {
    // Search for all files that match the pattern
    const files = await glob(searchPattern);
    // For each file that matches the pattern
    for (const file of files) {
      try {
        // Read the file
        let data = await fs.readFile(file, 'utf8');

        // Replace all instances of the search string with the replacement string
        let result = data.split(searchString).join(replacementString);

        // Write the result back to the file
        await fs.writeFile(file, result, 'utf8');
      } catch (err) {
        console.error('Error while processing file:', err);
      }
    }
  } catch (e) {
    console.error('Error while searching for files:', e);
  }
})();
