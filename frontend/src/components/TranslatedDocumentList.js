import React, { useEffect, useState } from 'react';
import { Button, Grid, Typography } from '@mui/material';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const TranslatedDocumentList = () => {
  const [documents, setDocuments] = useState([]);

  const fetchDocuments = async () => {
    try {
      const response = await fetch('http://34.194.38.37:8080/api/translated-documents');
      const data = await response.json();
      setDocuments(data);
    } catch (error) {
      console.error('Error fetching documents:', error);
    }
  };

  useEffect(() => {
    fetchDocuments();
  }, []);

  const handleDownload = async (documentId) => {
    try {
      toast.success('Downloading has started', { position: toast.POSITION.TOP_CENTER });
      const response = await fetch(`http://34.194.38.37:8080/api/download/${documentId}`, {
        method: 'POST',
        responseType: 'blob',
      });

      if (response.ok) {
        console.log('Downloading began successfully!');
        const blob = await response.blob();
        const url = window.URL.createObjectURL(new Blob([blob]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', documentId);
        document.body.appendChild(link);
        link.click();
        link.parentNode.removeChild(link);
      } else {
        console.error('Download failed:', response.statusText);
      }
    } catch (error) {
      console.error('Download failed:', error);
    }
  };

  return (
    <>
      <ToastContainer />
      <div style={{ border: '2px solid #ccc', marginTop: '80px', marginLeft: '180px', width: '400px' }}>
        <Typography variant="h6" style={{ fontWeight: 'bold', paddingTop: '20px', paddingLeft: '10px' }}>
          Translated Documents:
        </Typography>
        {documents.map((document, index) => (
          <Grid container key={index} alignItems="center" spacing={2} style={{ marginTop: '5px', marginBottom: '5px', padding: '10px' }}>
            <Grid item xs={6}>
              <Typography>{document}</Typography>
            </Grid>
            <Grid item xs={3}>
              <Button onClick={() => handleDownload(document)} variant="contained" color="primary" style={{ marginLeft: '40px' }}>
                Download
              </Button>
            </Grid>
          </Grid>
        ))}
      </div>
    </>
  );
};

export default TranslatedDocumentList;
