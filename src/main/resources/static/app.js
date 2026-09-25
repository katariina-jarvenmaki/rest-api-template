const rows = document.querySelector("#item-rows");
const emptyMessage = document.querySelector("#empty-message");
const listView = document.querySelector("#list-view");
const itemView = document.querySelector("#item-view");
const itemName = document.querySelector("#item-name");
const itemDescription = document.querySelector("#item-description");
const itemError = document.querySelector("#item-error");
const backButton = document.querySelector("#back-button");
const addButton = document.querySelector("#add-button");
const itemEditButton = document.querySelector("#item-edit-button");
const itemDeleteButton = document.querySelector("#item-delete-button");
const formView = document.querySelector("#form-view");
const formTitle = document.querySelector("#form-title");
const nameInput = document.querySelector("#name-input");
const descriptionInput = document.querySelector("#description-input");
const formError = document.querySelector("#form-error");
const saveButton = document.querySelector("#save-button");
const cancelButton = document.querySelector("#cancel-button");
const deleteDialog = document.querySelector("#delete-dialog");
const deleteMessage = document.querySelector("#delete-message");
const deleteYesButton = document.querySelector("#delete-yes-button");
const deleteCancelButton = document.querySelector("#delete-cancel-button");

// null = add mode, a number = edit mode for that item
let editingId = null;
// The item shown in the item view, needed when its Edit button is clicked
let viewingId = null;
// null = no delete pending, a number = the id the popup would DELETE
let pendingDeleteId = null;

function showListView() {
    listView.hidden = false;
    itemView.hidden = true;
    formView.hidden = true;
}

function showItemView() {
    listView.hidden = true;
    itemView.hidden = false;
    formView.hidden = true;
}

function showFormView() {
    listView.hidden = true;
    itemView.hidden = true;
    formView.hidden = false;
}

// Redraws the list view: fetches GET /items, then rebuilds every row
async function loadItems() {

    const response = await fetch("/items");
    const items = await response.json();
    rows.innerHTML = "";

    for (const item of items) {

        const tr = document.createElement("tr");
        const tdId = document.createElement("td");
        tdId.textContent = item.id;
        const tdName = document.createElement("td");
        tdName.textContent = item.name;
        const tdDesc = document.createElement("td");
        tdDesc.textContent = item.description;
        const tdActions = document.createElement("td");
        const viewButton = document.createElement("button");
        const editButton = document.createElement("button");
        editButton.textContent = "Edit";
        editButton.addEventListener("click", () => openForm(item.id));
        tdActions.append(editButton);
        const deleteButton = document.createElement("button");
        deleteButton.textContent = "Delete";
        deleteButton.addEventListener("click", () => openDeleteDialog(item.id));
        tdActions.append(deleteButton);
        viewButton.textContent = "View";
        viewButton.addEventListener("click", () => showItem(item.id));
        tdActions.append(viewButton);
        tr.append(tdId, tdName, tdDesc, tdActions);
        rows.append(tr);

    }
    emptyMessage.hidden = items.length > 0;
}

// Fetches one item and fills the item view
async function showItem(id) {
    const response = await fetch("/items/" + id);

    if (response.ok) {
        const item = await response.json();
        itemName.textContent = item.name;
        itemDescription.textContent = item.description;
        itemName.hidden = false;
        itemDescription.hidden = false;
        itemError.hidden = true;
        viewingId = id;
    } else {
        if (response.status === 404) {
            itemError.textContent = "Item " + id + " does not exist.";
        } else {
            itemError.textContent = "Loading failed (status " + response.status + ").";
        }
        itemName.hidden = true;
        itemDescription.hidden = true;
        itemError.hidden = false;
    }

    showItemView();
}

// Opens the form either empty (add) or prefilled (edit)
function openForm(id) {

    if (id === undefined) {
        editingId = null;
        formTitle.textContent = "Add item";
        nameInput.value = "";
        descriptionInput.value = "";
    } else {
        editingId = id;
        formTitle.textContent = "Edit item";
        nameInput.value = "";
        descriptionInput.value = "";
        fillForm(id);
    }

    formError.hidden = true;
    showFormView();
}

// Fetches the item and fills the edit form, runs after openForm shows it
async function fillForm(id) {
    const response = await fetch("/items/" + id);

    if (response.ok) {
        const item = await response.json();
        nameInput.value = item.name;
        descriptionInput.value = item.description;
    } else {
        formError.textContent = "Loading failed (status " + response.status + ").";
        formError.hidden = false;
    }
}

// Sends the form: POST for add, PUT for edit, then back to the list
async function saveForm() {

    // Browser-side check: the backend returns 400 for an empty name
    if (nameInput.value.trim() === "") {
        formError.textContent = "Name must not be empty.";
        formError.hidden = false;
        return;
    }

    const body = JSON.stringify({
        name: nameInput.value,
        description: descriptionInput.value
    });

    let response;
    if (editingId === null) {
        response = await fetch("/items", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: body
        });
    } else {
        response = await fetch("/items/" + editingId, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: body
        });
    }

    if (response.ok) {
        showListView();
        loadItems();
    } else {
        formError.textContent = "Saving failed (status " + response.status + ").";
        formError.hidden = false;
    }
}

// Fills the message with the id and shows the popup over the current view
function openDeleteDialog(id) {
    pendingDeleteId = id;
    deleteMessage.textContent = "Delete item " + id + "?";
    deleteDialog.showModal();
}

// Yes button: sends DELETE, then closes and refreshes the list
async function confirmDelete() {

    // Nothing pending = the popup was already handled, just close it
    if (pendingDeleteId === null) {
        deleteDialog.close();
        return;
    }

    const response = await fetch("/items/" + pendingDeleteId, {
        method: "DELETE"
    });

    // 404 = someone else already deleted it, the refresh removes the stale row either way
    if (response.ok || response.status === 404) {
        pendingDeleteId = null;
        deleteDialog.close();
        showListView();
        loadItems();
    } else {
        deleteMessage.textContent = "Delete failed (status " + response.status + ").";
    }
}

backButton.addEventListener("click", () => {
    showListView();
    loadItems();
});

addButton.addEventListener("click", () => openForm());

itemEditButton.addEventListener("click", () => openForm(viewingId));

itemDeleteButton.addEventListener("click", () => openDeleteDialog(viewingId));

saveButton.addEventListener("click", saveForm);
cancelButton.addEventListener("click", () => {
    showListView();
    loadItems();
});

deleteYesButton.addEventListener("click", confirmDelete);
deleteCancelButton.addEventListener("click", () => deleteDialog.close());

loadItems();
