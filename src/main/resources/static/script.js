async function cargarPedidos() {
    const tbody = document.getElementById('tabla-pedidos');
    tbody.innerHTML = '<tr><td colspan="5" class="sin-datos">Cargando...</td></tr>';

    try {
        const response = await fetch('/api/pedido');
        const pedidos = await response.json();

        if (pedidos.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="sin-datos">No hay pedidos registrados</td></tr>';
            return;
        }

        let filas = '';
        for (const pedido of pedidos) {
            filas += `
                <tr>
                    <td>#${pedido.id}</td>
                    <td>${pedido.user ? pedido.user.nombre : '—'}</td>
                    <td>${pedido.user ? pedido.user.email : '—'}</td>
                    <td>${formatearFechaHora(pedido.fechaHora)}</td>
                    <td><span class="estado-${pedido.estado.toLowerCase()}">${pedido.estado}</span></td>
                </tr>
            `;
        }
        tbody.innerHTML = filas;

    } catch (error) {
        tbody.innerHTML = '<tr><td colspan="5" class="error">Error al cargar los pedidos. ¿Está el servidor arrancado?</td></tr>';
    }
}

async function cargarReservas() {
    const tbody = document.getElementById('tabla-reservas');
    tbody.innerHTML = '<tr><td colspan="7" class="sin-datos">Cargando...</td></tr>';

    try {
        const response = await fetch('/api/reserva');
        const reservas = await response.json();

        if (reservas.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="sin-datos">No hay reservas registradas</td></tr>';
            return;
        }

        let filas = '';
        for (const reserva of reservas) {
            filas += `
                <tr>
                    <td>#${reserva.id}</td>
                    <td>${reserva.usuario ? reserva.usuario.nombre : '—'}</td>
                    <td>${reserva.fecha}</td>
                    <td>${reserva.hora}</td>
                    <td>${reserva.comensales} personas</td>
                    <td><span class="estado-${reserva.estado.toLowerCase()}">${reserva.estado}</span></td>
                    <td>${botonesReserva(reserva)}</td>
                </tr>
            `;
        }
        tbody.innerHTML = filas;

    } catch (error) {
        tbody.innerHTML = '<tr><td colspan="7" class="error">Error al cargar las reservas. ¿Está el servidor arrancado?</td></tr>';
    }
}

function botonesReserva(reserva) {
    if (reserva.estado === 'Pendiente') {
        return `
            <button class="btn btn-aceptar" onclick="cambiarEstadoReserva(${reserva.id}, 'Aceptada')">Aceptar</button>
            <button class="btn btn-denegar" onclick="cambiarEstadoReserva(${reserva.id}, 'Denegada')">Denegar</button>
        `;
    }
    return '—';
}

async function cambiarEstadoReserva(id, nuevoEstado) {
    try {
        const response = await fetch(`/api/reserva/${id}`);
        const reserva = await response.json();

        reserva.estado = nuevoEstado;
        await fetch(`/api/reserva/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(reserva)
        });

        cargarReservas();

    } catch (error) {
        alert('Error al actualizar la reserva');
    }
}

function formatearFechaHora(fechaHora) {
    if (!fechaHora) return '—';
    const fecha = new Date(fechaHora);
    return fecha.toLocaleDateString('es-ES') + ' ' + fecha.toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' });
}

cargarPedidos();
cargarReservas();