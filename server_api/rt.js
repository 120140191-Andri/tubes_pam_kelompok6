const express = require("express");

const router = express.Router();

const controllerswallet = require("./wallet");
router.route("/asset/:alamat").get(controllerswallet.walletasset);

module.exports = router;