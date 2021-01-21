const mongoose = require('mongoose');

// Model //
const model = new mongoose.Schema({
    groupId: { type: Number, required: true },
    permission: { type: String, required: true }
});

module.exports = mongoose.model('permissions', model);