import React, { useEffect, useState } from 'react';
import { Button, Grid, Typography } from '@mui/material';
import { useHistory } from 'react-router-dom';
import 'react-toastify/dist/ReactToastify.css';

const DocumentList = () => {
  const [documents, setDocuments] = useState([]);
  const history = useHistory();

  const fetchDocuments = async () => {
    try {
      const response = await fetch('http://34.194.38.37:8080/api/documents');
      const data = await response.json();
      setDocuments(data);
    } catch (error) {
      console.error('Error fetching documents:', error);
    }
  };

  useEffect(() => {
    fetchDocuments();
  }, []);

  const startTranslation = (documentId) => {
    history.push({
      pathname: '/translate',
      state: { documentId: documentId }
    });
  };

  return (
    <>
      <div style={{ border: '2px solid #ccc', marginTop: '80px', marginLeft: '180px', width: '400px' }}>
        <Typography variant="h6" style={{ fontWeight: 'bold', paddingTop: '20px', paddingLeft: '10px' }}>
          Uploaded Documents:
        </Typography>
        {documents.map((document, index) => (
          <Grid
            container
            key={index}
            alignItems="center"
            spacing={2}
            style={{ marginTop: '5px', marginBottom: '5px', padding: '10px' }}
          >
            <Grid item xs={6}>
              <Typography>{document}</Typography>
            </Grid>
            <Grid item xs={3}>
              <Button
                onClick={() => startTranslation(document)}
                variant="contained"
                color="primary"
                style={{ marginLeft: '40px' }}
              >
                Translate
              </Button>
            </Grid>
          </Grid>
        ))}
      </div>
    </>
  );
};

export default DocumentList;
