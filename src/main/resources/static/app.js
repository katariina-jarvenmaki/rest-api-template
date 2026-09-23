// These ids must match index.html
const rows = document.querySelector("#item-rows");
const emptyMessage = document.querySelector("#empty-message");

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
        tr.append(tdId, tdName, tdDesc);
        rows.append(tr);
    
    }
    emptyMessage.hidden = items.length > 0;
}

// The page exists when this runs
loadItems();
