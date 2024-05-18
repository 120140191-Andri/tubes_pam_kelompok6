const path = require('path')
const readXlsxFile = require('read-excel-file/node')
const multer = require('multer')
const axios = require('axios')

exports.walletasset = (req, res, next) => {
    if (!req.params.alamat) {
        return next(new AppError("No apenda id found", 404));
    }
    
    const API_KEY = 'mainnetl82qjEr1AsW7meAAnUE7cTEtNCEuUI69';
    const BASE_URL = 'https://cardano-mainnet.blockfrost.io/api/v0';
    const addr = req.params.alamat;
    let notif = [];
    let wallet = [];
    
    async function getAssetsInfo(accountAddress) {
        try {
            const response = await axios.get(`${BASE_URL}/assets/${accountAddress}`, {
                headers: { 'project_id': API_KEY }
            });
            return response.data.metadata;
        } catch (error) {
            console.error('Failed to retrieve account info:', error.response);
            return null;
        }
    }
    
    async function getAccountInfo(accountAddress) {
            
            try{
                
                const response = await axios.get(`${BASE_URL}/addresses/${accountAddress}`, {
                    headers: { 'project_id': API_KEY }
                });
                
                var ada = false;
                dats = [];
                
                for (const item of response.data.amount) {
                    
                    let now = new Date();
                    let waktu = now;
                    
                    if(item.unit != 'lovelace'){
                        
                        if(item.unit == "1d7f33bd23d85e1a25d87d86fac4f199c3197a2f7afeb662a0f34e1e776f726c646d6f62696c65746f6b656e"){
                            
                            let faktor  = Math.pow(10, 6)
                            let jumnotif = parseFloat((item.quantity / faktor).toFixed(4))
                            
                            if (wallet.length != 0) {
                                for(const dt of wallet) {
                                    if(dt.nama == "WMT"){
                                        if(dt.jumlah > jumnotif){
                                            notif.push({ 
                                                status: 'minus', 
                                                pesan: `-${ parseFloat(dt.jumlah - jumnotif).toFixed(4) } WMT`, 
                                                waktu, 
                                            });
                                        }else if(dt.jumlah < jumnotif){
                                            notif.push({ 
                                                status: 'plus', 
                                                pesan: `+${ parseFloat(jumnotif - dt.jumlah).toFixed(4) } WMT`, 
                                                waktu, 
                                            });
                                        }
                                    } 
                                }
                            }
            
                            dats.push( { nama: 'WMT', jumlah: jumnotif } );    
                        }else if(item.unit == "884892bcdc360bcef87d6b3f806e7f9cd5ac30d999d49970e7a903ae5041564941"){
                            
                            let faktor  = Math.pow(10, 0)
                            let jumnotif = parseFloat((item.quantity / faktor).toFixed(4))
                            
                            if (wallet.length != 0) {
                                for(const dt of wallet) {
                                    if(dt.nama == "PAVIA"){
                                        if(dt.jumlah > jumnotif){
                                            notif.push({ 
                                                status: 'minus', 
                                                pesan: `-${ parseFloat(dt.jumlah - jumnotif).toFixed(4) } PAVIA`, 
                                                waktu, 
                                            });
                                        }else if(dt.jumlah < jumnotif){
                                            notif.push({ 
                                                status: 'plus', 
                                                pesan: `+${ parseFloat(jumnotif - dt.jumlah).toFixed(4) } PAVIA`, 
                                                waktu, 
                                            });
                                        }
                                    } 
                                }
                            }
            
                            dats.push( { nama: 'PAVIA', jumlah: jumnotif } );    
                        }else{
                            let dt = await getAssetsInfo(item.unit)
        
                            let des = dt.decimals
                            let faktor  = Math.pow(10, des)
                            let jumnotif = parseFloat((item.quantity / faktor).toFixed(4))
                            
                            if (wallet.length != 0) {
                                for(const dts of wallet) {
                                    if(dts.nama == dt.ticker){
                                        if(dts.jumlah > jumnotif){
                                            notif.push({ 
                                                status: 'minus', 
                                                pesan: `-${ parseFloat(dts.jumlah - jumnotif).toFixed(4) } ${dt.ticker}`, 
                                                waktu, 
                                            });
                                        }else if(dts.jumlah < jumnotif){
                                            notif.push({ 
                                                status: 'plus', 
                                                pesan: `+${ parseFloat(jumnotif - dts.jumlah).toFixed(4) } ${dt.ticker}`, 
                                                waktu, 
                                            });
                                        }
                                    } 
                                }
                            }
            
                            dats.push( { nama: dt.ticker, jumlah: jumnotif } );
                        }
                        
                    }else{
                        
                        if(ada == false){
                            let jumnotif = parseFloat((item.quantity / 1000000).toFixed(4));
                        
                            if (wallet.length != 0) {
                                for(const dts of wallet) {
                                    if(dts.nama == '₳'){
                                        if(dts.jumlah > jumnotif){
                                            notif.push({ 
                                                status: 'minus', 
                                                pesan: `-${ parseFloat(dts.jumlah - jumnotif).toFixed(4) } ₳`, 
                                                waktu, 
                                            });
                                        }else if(dts.jumlah < jumnotif){
                                            notif.push({ 
                                                status: 'plus', 
                                                pesan: `+${ parseFloat(jumnotif - dts.jumlah).toFixed(4) } ₳`, 
                                                waktu, 
                                            });
                                        }
                                    } 
                                }
                            }
                            
                            dats.push( { nama: '₳', jumlah: jumnotif } );
                            ada = true;
                        }
                        
                    }
                };        
                
                wallet = [];
                wallet = dats;
                
                let hrg = 0;
                const url = 'https://api.binance.com/api/v3/ticker/price?symbol=ADAUSDT';

                axios.get(url)
                  .then((response) => {
                    // Status code 200 menunjukkan permintaan berhasil
                    if (response.status === 200) {
                      const data = response.data;
                      
                      hrg = data.price;
                      
                      res.status(200).json({
                        status: 'ok',
                        harga_ada: hrg,
                        data: wallet,
                      });
                      
                    } else {
                      console.error('Terjadi kesalahan:', response.statusText);
                    }
                  })
                  .catch((error) => {
                    console.error('Kesalahan saat mengambil data:', error.message);
                    res.status(200).json({
                        status: 'ok',
                        harga_ada: 0,
                        data: wallet,
                    });
                  });
                
            } catch (error) {
                console.error('Failed to retrieve account info:', error);
                res.status(200).json({
                    status: 'error',
                    msg: error
                });
            }
            
    }
    
    getAccountInfo(addr);

};