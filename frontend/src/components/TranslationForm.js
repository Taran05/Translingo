import React, { useState } from 'react';
import { Button, Grid, Select, MenuItem, Typography } from '@mui/material';
import { useHistory } from 'react-router-dom';

const TranslationForm = ({ documentId }) => {
  const [sourceLanguage, setSourceLanguage] = useState('');
  const [targetLanguage, setTargetLanguage] = useState('');

  const history = useHistory();

  const handleSourceLanguageChange = (event) => {
    const selectedSourceLanguage = event.target.value;
    setSourceLanguage(selectedSourceLanguage);

    // Clear the target language if it matches the selected source language
    if (targetLanguage === selectedSourceLanguage) {
      setTargetLanguage('');
    }
  };

  const handleTargetLanguageChange = (event) => {
    const selectedTargetLanguage = event.target.value;
    setTargetLanguage(selectedTargetLanguage);

    // Clear the source language if it matches the selected target language
    if (sourceLanguage === selectedTargetLanguage) {
      setSourceLanguage('');
    }
  };

  const handleTranslate = async () => {
    try {
      const response = await fetch(`http://34.194.38.37:8080/api/translate/${documentId}`, {
        method: 'POST',
        body: JSON.stringify({ sourceLanguage, targetLanguage }),
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (response.ok) {
        console.log('Translation initiated successfully!');
        // Redirect the user to TranslationStatus component
        history.push('/translations');
      } else {
        console.error('Translation initiation failed:', response.statusText);
      }
    } catch (error) {
      console.error('Translation initiation failed:', error);
    }
  };

  // Mapping of language codes to language names
  const languageNames = {
    en: 'English',
    fr: 'French',
    es: 'Spanish',
    it: 'Italian',
    'pt-PT': 'Portuguese (Portugal)',
    pa: 'Punjabi',
    ja: 'Japanese',
    de: 'German',
    ar: 'Arabic',
    zh: 'Chinese (Simplified)',
  };

  // Get the list of target languages excluding the selected source language
  const targetLanguageOptions = Object.keys(languageNames).filter((lang) => lang !== sourceLanguage);

  return (
    <Grid container spacing={2}>
      <Typography
        variant="h6"
        style={{
          fontWeight: 'bold',
          paddingTop: '80px',
          paddingBottom: '20px',
          paddingLeft: '280px',
        }}
      >
        Select Source & Target Languages:
      </Typography>
      <Grid item xs={12} style={{ display: 'flex', justifyContent: 'center' }}>
        <Select
          value={sourceLanguage}
          onChange={handleSourceLanguageChange}
          style={{ width: '350px', marginBottom: '20px' }}
          displayEmpty
        >
          <MenuItem value="" disabled>
            Select Source Language
          </MenuItem>
          {Object.entries(languageNames).map(([code, name]) => (
            <MenuItem key={code} value={code}>
              {name}
            </MenuItem>
          ))}
        </Select>
      </Grid>
      <Grid item xs={12} style={{ display: 'flex', justifyContent: 'center' }}>
        <Select
          value={targetLanguage}
          onChange={handleTargetLanguageChange}
          style={{ width: '350px', marginBottom: '20px' }}
          displayEmpty
        >
          <MenuItem value="" disabled>
            Select Target Language
          </MenuItem>
          {targetLanguageOptions.map((code) => (
            <MenuItem key={code} value={code}>
              {languageNames[code]}
            </MenuItem>
          ))}
        </Select>
      </Grid>
      <Grid item xs={12} style={{ display: 'flex', justifyContent: 'center' }}>
        <Button
          onClick={handleTranslate}
          variant="contained"
          color="primary"
          style={{ width: '350px', padding: '7px' }}
          disabled={!sourceLanguage || !targetLanguage}
        >
          Translate
        </Button>
      </Grid>
    </Grid>
  );
};

export default TranslationForm;
