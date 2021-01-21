const express = require('express');
const model = require('../models/PermissionsModel');

// Route //
const route = express.Router();

// Get Permissions //
route.get('/get', async (req, res) => {
    const query = await model.find();
    const result = format(query);

    return res.json({ success: true, response: result });
});

// Get By Id //
route.get('/get/:groupId', async (req, res) => {
    const query = await model.find({ groupId: parseInt(req.params.groupId) });
    const result = format(query);

    return res.json({ success: true, response: result });
});

// Create //
route.get('/create/:groupId/:permission', async (req, res) => {
    const groupId = parseInt(req.params.groupId);
    const permission = req.params.permission;
    const query = await model.find({ groupId: groupId, permission: permission });
    if (query.length > 0) {
        return res.json({ success: false, message: 'Esta permissão já está criada para este grupo!' })
    }

    await model.create({ groupId: groupId, permission: permission });
    return res.json({ success: true, message: `Permissão '${permission}' para o group de ID ${groupId} criada com sucesso!` })
});

// Remove //
route.get('/remove/:groupId/:permission', async (req, res) => {
    const groupId = parseInt(req.params.groupId);
    const permission = req.params.permission;
    const query = await model.find({ groupId: groupId, permission: permission });
    if (query.length < 1) {
        return res.json({ success: false, message: 'Esta permissão não foi setada para este grupo!' });
    }

    await model.remove({ groupId: groupId, permission: permission });
    return res.json({ success: true, message: `Permissão '${permission}' do group de ID ${groupId} removida com sucesso!` })
});

// Format //
function format(query) {
    const result = [];

    for (var i in query) {
        const data = query[i];
        const groupId = data['groupId'];
        const permission = data['permission'];

        result.push({ groupId: groupId, permission: permission });
    }

    return result;
}

module.exports = route;