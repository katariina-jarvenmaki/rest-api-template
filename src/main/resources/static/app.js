// These ids must match index.html
const rows = document.querySelector("#item-rows");
const emptyMessage = document.querySelector("#empty-message");
const listView = document.querySelector("#list-view");
const itemView = document.querySelector("#item-view");
const itemName = document.querySelector("#item-name");
const itemDescription = document.querySelector("#item-description");
const itemError = document.querySelector("#item-error");
const backButton = document.querySelector("#back-button");

// Exactly one view is visible at a time
function showListView() {
    listView.hidden = false;
    itemView.hidden = true;
}

function showItemView() {
    listView.hidden = true;
    itemView.hidden = false;
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

// Back returns to the list and refreshes it
backButton.addEventListener("click", () => {
    showListView();
    loadItems();
});

// The page exists when this runs
loadItems();