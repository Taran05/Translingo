import React, { useState } from 'react';
import { Button, Grid, Typography } from '@mui/material';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import './DocumentUploadForm.css';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const DocumentUploadForm = ({ onUpload }) => {
  const [selectedFile, setSelectedFile] = useState(null);

  const handleFileChange = (event) => {
    setSelectedFile(event.target.files[0]);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (!selectedFile) return;

    try {
      const formData = new FormData();
      formData.append('file', selectedFile);

      const response = await fetch('http://34.194.38.37:8080/api/upload', {
        method: 'POST',
        body: formData,
      });

      if (response.ok) {
        console.log('File uploaded successfully!');
        toast.success('File Uploaded Successfully', {
          position: toast.POSITION.TOP_CENTER,
        });

        setTimeout(() => {
          // Trigger the parent component's onUpload function
          onUpload();
          // Redirect the user
          window.location.href = '/';
        }, 3000); // 3 seconds delay
      } else {
        console.error('File upload failed:', response.statusText);
      }
    } catch (error) {
      console.error('File upload failed:', error);
    }
  };

  return (
    <>
      <ToastContainer
        position="top-center"
        autoClose={3000}
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
        toastContainerClassName="toast-container"
        toastClassName="toast"
      />
      <form onSubmit={handleSubmit}>
        <Grid container spacing={2}>
          <Grid item xs={12}>
            <Typography variant="h5" style={{ fontWeight: 'bold', textAlign: 'center', paddingTop: '90px', paddingBottom: '20px'}}>
              Convert ONE language to OTHER
            </Typography>
            <Typography variant="body" color="textSecondary" style={{ textAlign: 'center', fontSize: '20px', paddingLeft: '25px' }}>
              Easily convert from one language to other
            </Typography>
          </Grid>
          {selectedFile ? (
            <Grid item xs={12} style={{ display: 'flex', justifyContent: 'center', marginTop: '40px', marginLeft: '10px' }}>
              <Typography variant="body" className="selected-file">
                {selectedFile.name}
              </Typography>
            </Grid>
          ) : (
            <Grid item xs={12} md={6} style={{ display: 'flex', justifyContent: 'center', marginTop: '50px', marginLeft: '280px' }}>
              <label htmlFor="file-upload" style={{ display: 'flex', alignItems: 'center' }}>
                <input
                  id="file-upload"
                  type="file"
                  accept=".pdf, .doc, .docx"
                  onChange={handleFileChange}
                  style={{ display: 'none' }}
                />
                <Button variant="contained" color="primary" component="span" startIcon={<CloudUploadIcon />} style={{ padding: '10px', width: '380px', marginRight: '340px' }}>
                  Choose File
                </Button>
              </label>
            </Grid>
          )}
          {selectedFile && (
            <Grid item xs={12} style={{ display: 'flex', justifyContent: 'center', marginTop: '30px' }}>
              <Button type="submit" variant="contained" color="primary" style={{ padding: '10px', width: '380px' }}>
                Upload
              </Button>
            </Grid>
          )}
        </Grid>
      </form>
    </>
  );
};

export default DocumentUploadForm;
