// These ids must match index.html
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
const formView = document.querySelector("#form-view");
const formTitle = document.querySelector("#form-title");
const nameInput = document.querySelector("#name-input");
const descriptionInput = document.querySelector("#description-input");
const formError = document.querySelector("#form-error");
const saveButton = document.querySelector("#save-button");
const cancelButton = document.querySelector("#cancel-button");

// null = add mode, a number = edit mode for that item
let editingId = null;
// The item shown in the item view, needed when its Edit button is clicked
let viewingId = null;

// Exactly one view is visible at a time
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

    // await pauses the function until the API answers, then json() parses the body
    const response = await fetch("/items");
    const items = await response.json();
    rows.innerHTML = "";

    // Rebuilds the whole table from scratch
    for (const item of items) {

        // Rows are built as elements
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

    // id given = edit an existing item, no id = add a new one
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
        formError.textContent = "Loading failed (status " + response.status
+ ").";
        formError.hidden = false;
    }
}

// Sends the form: POST for add, PUT for edit, then back to the list
async function saveForm() {

    // Browser-side check: the backend 500s on an empty name today
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
        formError.textContent = "Saving failed (status " + response.status +
").";
        formError.hidden = false;
    }
}

// Back returns to the list and refreshes it
backButton.addEventListener("click", () => {
    showListView();
    loadItems();
});

// Add opens the form in add mode
addButton.addEventListener("click", () => openForm());

// Edit in the item view opens the form prefilled with the viewed item
itemEditButton.addEventListener("click", () => openForm(viewingId));

// Save and Cancel leave the form, Cancel discards
saveButton.addEventListener("click", saveForm);
cancelButton.addEventListener("click", () => {
    showListView();
    loadItems();
});

// The page exists when this runs
loadItems();