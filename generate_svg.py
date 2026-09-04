import html

def create_svg():
    width = 2600
    height = 1650

    svg = []
    svg.append(f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {width} {height}" width="{width}" height="{height}">')
    
    svg.append('''<defs>
        <style>
            @import url('https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;600;700&amp;family=Inter:wght@400;600;700;800&amp;display=swap');
            text { font-family: 'Inter', -apple-system, sans-serif; }
            .code-text { font-family: 'JetBrains Mono', monospace; font-size: 11.5px; }
            .title-text { font-family: 'Inter', sans-serif; font-weight: 800; }
            .shadow { filter: drop-shadow(0px 10px 28px rgba(0, 0, 0, 0.55)); }
        </style>
        
        <!-- Markers -->
        <marker id="arrow-slate" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="7" markerHeight="7" orient="auto-start-reverse">
            <path d="M 0 1 L 10 5 L 0 9 z" fill="#94a3b8" />
        </marker>
        <marker id="arrow-indigo" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="7" markerHeight="7" orient="auto-start-reverse">
            <path d="M 0 1 L 10 5 L 0 9 z" fill="#818cf8" />
        </marker>
        <marker id="arrow-amber" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="7" markerHeight="7" orient="auto-start-reverse">
            <path d="M 0 1 L 10 5 L 0 9 z" fill="#fbbf24" />
        </marker>
        <marker id="arrow-sky" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="7" markerHeight="7" orient="auto-start-reverse">
            <path d="M 0 1 L 10 5 L 0 9 z" fill="#38bdf8" />
        </marker>
        <marker id="arrow-pink" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="7" markerHeight="7" orient="auto-start-reverse">
            <path d="M 0 1 L 10 5 L 0 9 z" fill="#f472b6" />
        </marker>
        <marker id="arrow-emerald" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="7" markerHeight="7" orient="auto-start-reverse">
            <path d="M 0 1 L 10 5 L 0 9 z" fill="#34d399" />
        </marker>
        <marker id="arrow-purple" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="7" markerHeight="7" orient="auto-start-reverse">
            <path d="M 0 1 L 10 5 L 0 9 z" fill="#c084fc" />
        </marker>

        <!-- Realization Hollow Triangle -->
        <marker id="realization" viewBox="0 0 14 14" refX="13" refY="7" markerWidth="11" markerHeight="11" orient="auto-start-reverse">
            <polygon points="1,1 13,7 1,13" fill="#0b0f19" stroke="#ec4899" stroke-width="1.8" />
        </marker>

        <!-- Composition Diamonds -->
        <marker id="diamond-emerald" viewBox="0 0 16 16" refX="0" refY="8" markerWidth="12" markerHeight="12" orient="auto-start-reverse">
            <polygon points="0,8 8,2 16,8 8,14" fill="#10b981" />
        </marker>
        <marker id="diamond-sky" viewBox="0 0 16 16" refX="0" refY="8" markerWidth="12" markerHeight="12" orient="auto-start-reverse">
            <polygon points="0,8 8,2 16,8 8,14" fill="#0ea5e9" />
        </marker>
        <marker id="diamond-amber" viewBox="0 0 16 16" refX="0" refY="8" markerWidth="12" markerHeight="12" orient="auto-start-reverse">
            <polygon points="0,8 8,2 16,8 8,14" fill="#f59e0b" />
        </marker>
        <marker id="diamond-pink" viewBox="0 0 16 16" refX="0" refY="8" markerWidth="12" markerHeight="12" orient="auto-start-reverse">
            <polygon points="0,8 8,2 16,8 8,14" fill="#ec4899" />
        </marker>

        <!-- Background Pattern -->
        <pattern id="grid" width="32" height="32" patternUnits="userSpaceOnUse">
            <circle cx="16" cy="16" r="1.2" fill="#334155" opacity="0.4" />
        </pattern>
        
        <!-- Gradients -->
        <linearGradient id="grad-indigo" x1="0" y1="0" x2="1" y2="1">
            <stop offset="0%" stop-color="#4338ca" />
            <stop offset="100%" stop-color="#6366f1" />
        </linearGradient>
        <linearGradient id="grad-emerald" x1="0" y1="0" x2="1" y2="1">
            <stop offset="0%" stop-color="#047857" />
            <stop offset="100%" stop-color="#10b981" />
        </linearGradient>
        <linearGradient id="grad-sky" x1="0" y1="0" x2="1" y2="1">
            <stop offset="0%" stop-color="#0369a1" />
            <stop offset="100%" stop-color="#0ea5e9" />
        </linearGradient>
        <linearGradient id="grad-amber" x1="0" y1="0" x2="1" y2="1">
            <stop offset="0%" stop-color="#b45309" />
            <stop offset="100%" stop-color="#f59e0b" />
        </linearGradient>
        <linearGradient id="grad-pink" x1="0" y1="0" x2="1" y2="1">
            <stop offset="0%" stop-color="#be185d" />
            <stop offset="100%" stop-color="#ec4899" />
        </linearGradient>
        <linearGradient id="grad-purple" x1="0" y1="0" x2="1" y2="1">
            <stop offset="0%" stop-color="#6d28d9" />
            <stop offset="100%" stop-color="#8b5cf6" />
        </linearGradient>
    </defs>''')

    # Background
    svg.append(f'<rect width="{width}" height="{height}" fill="#0b0f19" />')
    svg.append(f'<rect width="{width}" height="{height}" fill="url(#grid)" />')

    # Header Title Banner
    svg.append('''<g transform="translate(60, 35)">
        <rect x="0" y="0" width="2480" height="95" rx="14" fill="#111827" stroke="#1e293b" stroke-width="1.8" class="shadow" />
        <rect x="0" y="0" width="8" height="95" rx="4" fill="#6366f1" />
        <text x="32" y="44" font-size="25" font-weight="800" fill="#f8fafc" letter-spacing="0.5">RESTAURANT MANAGEMENT SYSTEM — LOW LEVEL DESIGN (CLASS DIAGRAM)</text>
        <text x="32" y="72" font-size="13.5" font-weight="500" fill="#94a3b8">Complete Object-Oriented Architecture with Design Patterns (Facade &amp; Command) and Concurrency Blueprint</text>
        
        <g transform="translate(1780, 26)">
            <rect x="0" y="0" width="125" height="42" rx="8" fill="#1e1b4b" stroke="#4338ca" />
            <text x="62" y="26" font-size="11" font-weight="700" fill="#c7d2fe" text-anchor="middle">FACADE PATTERN</text>
        </g>
        <g transform="translate(1920, 26)">
            <rect x="0" y="0" width="140" height="42" rx="8" fill="#831843" stroke="#be185d" />
            <text x="70" y="26" font-size="11" font-weight="700" fill="#fbcfe8" text-anchor="middle">COMMAND PATTERN</text>
        </g>
        <g transform="translate(2075, 26)">
            <rect x="0" y="0" width="165" height="42" rx="8" fill="#064e3b" stroke="#047857" />
            <text x="82" y="26" font-size="11" font-weight="700" fill="#a7f3d0" text-anchor="middle">SECTION 9 THREAD-SAFE</text>
        </g>
        <g transform="translate(2255, 26)">
            <rect x="0" y="0" width="130" height="42" rx="8" fill="#1e293b" stroke="#475569" />
            <text x="65" y="26" font-size="11" font-weight="700" fill="#cbd5e1" text-anchor="middle">JAVA 25 IN-MEMORY</text>
        </g>
    </g>''')

    # Helper function to render a UML class box
    def draw_class(x, y, w, h, header_color, stereotype, name, fields, methods, concurrency_badge=None):
        out = []
        out.append(f'<g transform="translate({x}, {y})" class="shadow">')
        out.append(f'<rect x="0" y="0" width="{w}" height="{h}" rx="10" fill="#111827" stroke="#1e293b" stroke-width="1.8" />')
        
        header_height = 50 if stereotype else 42
        out.append(f'<path d="M 0 10 Q 0 0 10 0 L {w-10} 0 Q {w} 0 {w} 10 L {w} {header_height} L 0 {header_height} Z" fill="{header_color}" />')
        
        if stereotype:
            out.append(f'<text x="{w/2}" y="20" font-size="10" font-weight="600" fill="#ffffff" opacity="0.9" text-anchor="middle" letter-spacing="1">&lt;&lt;{html.escape(stereotype)}&gt;&gt;</text>')
            out.append(f'<text x="{w/2}" y="39" font-size="15.5" font-weight="700" fill="#ffffff" text-anchor="middle">{html.escape(name)}</text>')
        else:
            out.append(f'<text x="{w/2}" y="27" font-size="15.5" font-weight="700" fill="#ffffff" text-anchor="middle">{html.escape(name)}</text>')
            
        if concurrency_badge:
            badge_w = len(concurrency_badge) * 6.5 + 16
            out.append(f'<g transform="translate({w - badge_w - 12}, 8)">')
            out.append(f'<rect x="0" y="0" width="{badge_w}" height="19" rx="4" fill="#0f172a" opacity="0.75" />')
            out.append(f'<text x="{badge_w/2}" y="13.5" font-size="9" font-weight="700" fill="#f8fafc" text-anchor="middle">{html.escape(concurrency_badge)}</text>')
            out.append('</g>')

        cur_y = header_height + 18
        
        # Fields
        for f in fields:
            vis = f[0]
            rest = f[1:]
            vis_color = "#38bdf8" if vis == "+" else ("#f43f5e" if vis == "-" else "#fbbf24")
            out.append(f'<text x="14" y="{cur_y}" class="code-text" font-weight="700" fill="{vis_color}">{vis}</text>')
            out.append(f'<text x="26" y="{cur_y}" class="code-text" fill="#cbd5e1">{html.escape(rest)}</text>')
            cur_y += 18.5

        cur_y += 3
        out.append(f'<line x1="0" y1="{cur_y}" x2="{w}" y2="{cur_y}" stroke="#1e293b" stroke-width="1.5" />')
        cur_y += 17

        # Methods
        for m in methods:
            vis = m[0]
            rest = m[1:]
            vis_color = "#38bdf8" if vis == "+" else ("#f43f5e" if vis == "-" else "#fbbf24")
            out.append(f'<text x="14" y="{cur_y}" class="code-text" font-weight="700" fill="{vis_color}">{vis}</text>')
            out.append(f'<text x="26" y="{cur_y}" class="code-text" fill="#e2e8f0">{html.escape(rest)}</text>')
            cur_y += 18.5

        out.append('</g>')
        return "\n".join(out)

    # -------------------------------------------------------------
    # 1. TOP CENTER: RESTAURANT FACADE
    # -------------------------------------------------------------
    svg.append(draw_class(
        x=950, y=165, w=660, h=370,
        header_color="url(#grad-indigo)",
        stereotype="facade",
        name="Restaurant",
        fields=[
            "- name: String",
            "- menu: Menu",
            "- layout: Layout",
            "- reservationManager: ReservationManager",
            "- orderManager: OrderManager"
        ],
        methods=[
            "+ Restaurant(name: String, menu: Menu, layout: Layout)",
            "+ findAvailableTimeSlots(start: LocalDateTime, end: LocalDateTime, size: int): LocalDateTime[]",
            "+ createScheduledReservation(partyName: String, size: int, time: LocalDateTime): Reservation",
            "+ createWalkInReservation(partyName: String, size: int): Reservation",
            "+ removeReservation(partyName: String, size: int, time: LocalDateTime): void",
            "+ orderItem(table: Table, item: MenuItem): void",
            "+ cancelItem(table: Table, item: MenuItem): void",
            "+ deliverItem(table: Table, item: MenuItem): void",
            "+ calculateTableBill(table: Table): BigDecimal",
            "+ getName(): String | getMenu(): Menu | getLayout(): Layout",
            "+ getReservationManager(): ReservationManager | getOrderManager(): OrderManager"
        ],
        concurrency_badge="STATELESS FACADE"
    ))

    # -------------------------------------------------------------
    # 2. COLUMN 1: MENU SUBSYSTEM (LEFT)
    # -------------------------------------------------------------
    svg.append(draw_class(
        x=60, y=280, w=350, h=195,
        header_color="url(#grad-emerald)",
        stereotype="registry",
        name="Menu",
        fields=[
            "- menuItems: ConcurrentMap<String, MenuItem>"
        ],
        methods=[
            "+ addItem(item: MenuItem): void",
            "+ getItem(name: String): MenuItem",
            "+ getMenuItems(): Map<String, MenuItem>"
        ],
        concurrency_badge="ConcurrentHashMap"
    ))

    svg.append(draw_class(
        x=60, y=560, w=350, h=220,
        header_color="url(#grad-emerald)",
        stereotype="immutable entity",
        name="MenuItem",
        fields=[
            "- name: String",
            "- description: String",
            "- price: BigDecimal",
            "- category: Category"
        ],
        methods=[
            "+ MenuItem(name: String, desc: String, price: BigDecimal, cat: Category)",
            "+ getName(): String",
            "+ getDescription(): String",
            "+ getPrice(): BigDecimal",
            "+ getCategory(): Category",
            "+ equals(o: Object): boolean",
            "+ hashCode(): int"
        ],
        concurrency_badge="Thread-Safe"
    ))

    svg.append(draw_class(
        x=60, y=860, w=350, h=150,
        header_color="url(#grad-emerald)",
        stereotype="enumeration",
        name="Category",
        fields=[
            "+ MAIN",
            "+ APPETIZER",
            "+ DESSERT"
        ],
        methods=[
            "+ values(): Category[]",
            "+ valueOf(name: String): Category"
        ]
    ))

    # -------------------------------------------------------------
    # 3. COLUMN 2: RESERVATION SUBSYSTEM (MID-LEFT)
    # -------------------------------------------------------------
    svg.append(draw_class(
        x=470, y=560, w=440, h=250,
        header_color="url(#grad-amber)",
        stereotype="domain manager",
        name="ReservationManager",
        fields=[
            "- layout: Layout",
            "- reservations: Set<Reservation>"
        ],
        methods=[
            "+ ReservationManager(layout: Layout)",
            "+ findAvailableTimeSlots(start: LocalDateTime, end: LocalDateTime, size: int): LocalDateTime[]",
            "+ createReservation(name: String, size: int, time: LocalDateTime): Reservation",
            "+ removeReservation(name: String, size: int, time: LocalDateTime): boolean",
            "+ getReservationByPartyName(partyName: String): Reservation",
            "+ getReservations(): Set<Reservation>",
            "+ getLayout(): Layout"
        ],
        concurrency_badge="Atomic Set & Retry"
    ))

    svg.append(draw_class(
        x=470, y=880, w=440, h=215,
        header_color="url(#grad-amber)",
        stereotype="immutable record",
        name="Reservation",
        fields=[
            "- partyName: String",
            "- partySize: int",
            "- time: LocalDateTime",
            "- assignedTable: Table"
        ],
        methods=[
            "+ Reservation(name: String, size: int, time: LocalDateTime, table: Table)",
            "+ getPartyName(): String",
            "+ getPartySize(): int",
            "+ getTime(): LocalDateTime",
            "+ getAssignedTable(): Table",
            "+ equals(o: Object): boolean",
            "+ hashCode(): int"
        ],
        concurrency_badge="Thread-Safe"
    ))

    # -------------------------------------------------------------
    # 4. COLUMN 3: TABLE & LAYOUT SUBSYSTEM (MID-RIGHT)
    # -------------------------------------------------------------
    svg.append(draw_class(
        x=970, y=560, w=450, h=230,
        header_color="url(#grad-sky)",
        stereotype="domain manager",
        name="Layout",
        fields=[
            "- tablesById: Map<Integer, Table>",
            "- tablesByCapacity: SortedMap<Integer, Set<Table>>"
        ],
        methods=[
            "+ Layout(tableCapacities: List<Integer>)",
            "+ Layout(tables: Collection<Table>)",
            "+ findAvailableTable(partySize: int, time: LocalDateTime): Table",
            "+ findCandidateTables(partySize: int, time: LocalDateTime): List<Table>",
            "+ getTableById(tableId: int): Table",
            "+ getTablesById(): Map<Integer, Table>",
            "+ getTablesByCapacity(): SortedMap<Integer, Set<Table>>"
        ],
        concurrency_badge="Safe Publication"
    ))

    svg.append(draw_class(
        x=970, y=860, w=450, h=330,
        header_color="url(#grad-sky)",
        stereotype="aggregate root",
        name="Table",
        fields=[
            "- tableId: int",
            "- capacity: int",
            "- reservations: ConcurrentMap<LocalDateTime, Reservation>",
            "- orderedItems: ConcurrentMap<MenuItem, List<OrderItem>>"
        ],
        methods=[
            "+ Table(tableId: int, capacity: int)",
            "+ getTableId(): int | getCapacity(): int",
            "+ isAvailableAt(time: LocalDateTime): boolean",
            "+ reserveIfAvailable(time: LocalDateTime, res: Reservation): boolean",
            "+ addReservation(res: Reservation): void",
            "+ removeReservation(time: LocalDateTime): boolean",
            "+ addOrder(item: MenuItem): OrderItem",
            "+ addOrder(item: MenuItem, quantity: int): void",
            "+ removeOrder(item: MenuItem): OrderItem",
            "+ calculateBillAmount(): BigDecimal",
            "+ getOrderedItems(): Map<MenuItem, List<OrderItem>>"
        ],
        concurrency_badge="putIfAbsent & compute"
    ))

    # -------------------------------------------------------------
    # 5. COLUMN 4: ORDER & COMMAND SUBSYSTEM (RIGHT)
    # -------------------------------------------------------------
    svg.append(draw_class(
        x=1680, y=280, w=400, h=195,
        header_color="url(#grad-pink)",
        stereotype="invoker",
        name="OrderManager",
        fields=[
            "- commandQueue: Queue<OrderCommand>"
        ],
        methods=[
            "+ addCommand(command: OrderCommand): void",
            "+ executeCommands(): void",
            "+ isQueueEmpty(): boolean"
        ],
        concurrency_badge="ConcurrentLinkedQueue"
    ))

    svg.append(draw_class(
        x=1680, y=560, w=400, h=140,
        header_color="url(#grad-pink)",
        stereotype="interface",
        name="OrderCommand",
        fields=[],
        methods=[
            "+ execute(): void"
        ],
        concurrency_badge="Command Pattern"
    ))

    # Concrete Commands Stacked Neatly
    svg.append(draw_class(
        x=1510, y=770, w=320, h=140,
        header_color="url(#grad-purple)",
        stereotype="command",
        name="SendToKitchenCommand",
        fields=[
            "- orderItem: OrderItem"
        ],
        methods=[
            "+ SendToKitchenCommand(item: OrderItem)",
            "+ execute(): void",
            "+ getOrderItem(): OrderItem"
        ]
    ))

    svg.append(draw_class(
        x=1860, y=770, w=320, h=140,
        header_color="url(#grad-purple)",
        stereotype="command",
        name="DeliverCommand",
        fields=[
            "- orderItem: OrderItem"
        ],
        methods=[
            "+ DeliverCommand(item: OrderItem)",
            "+ execute(): void",
            "+ getOrderItem(): OrderItem"
        ]
    ))

    svg.append(draw_class(
        x=2210, y=770, w=320, h=140,
        header_color="url(#grad-purple)",
        stereotype="command",
        name="CancelCommand",
        fields=[
            "- orderItem: OrderItem"
        ],
        methods=[
            "+ CancelCommand(item: OrderItem)",
            "+ execute(): void",
            "+ getOrderItem(): OrderItem"
        ]
    ))

    # OrderItem (Receiver)
    svg.append(draw_class(
        x=1680, y=990, w=420, h=250,
        header_color="url(#grad-pink)",
        stereotype="receiver entity",
        name="OrderItem",
        fields=[
            "- item: MenuItem",
            "- status: volatile Status"
        ],
        methods=[
            "+ OrderItem(item: MenuItem)",
            "+ getItem(): MenuItem",
            "+ getStatus(): Status",
            "+ synchronized sendToKitchen(): void",
            "+ synchronized deliverToCustomer(): void",
            "+ synchronized cancel(): void"
        ],
        concurrency_badge="volatile + synchronized"
    ))

    # Status (Enum)
    svg.append(draw_class(
        x=2170, y=990, w=220, h=175,
        header_color="url(#grad-pink)",
        stereotype="enumeration",
        name="Status",
        fields=[
            "+ PENDING",
            "+ SENT_TO_KITCHEN",
            "+ DELIVERED",
            "+ CANCELED"
        ],
        methods=[
            "+ values(): Status[]",
            "+ valueOf(name): Status"
        ]
    ))

    # -------------------------------------------------------------
    # RELATIONSHIP CONNECTORS & ANNOTATIONS
    # -------------------------------------------------------------
    lines = []

    def make_path(points, stroke="#94a3b8", width="2", dashed=False, marker_start=None, marker_end=None):
        d = "M " + " L ".join(f"{p[0]} {p[1]}" for p in points)
        dash = ' stroke-dasharray="6,5"' if dashed else ''
        ms = f' marker-start="url(#{marker_start})"' if marker_start else ''
        me = f' marker-end="url(#{marker_end})"' if marker_end else ''
        return f'<path d="{d}" fill="none" stroke="{stroke}" stroke-width="{width}"{dash}{ms}{me} />'

    def make_label(x, y, text, bg="#1e293b", text_color="#cbd5e1"):
        pad = len(text) * 3.7 + 10
        return f'''<g transform="translate({x}, {y})">
            <rect x="{-pad}" y="-11" width="{pad*2}" height="22" rx="5" fill="{bg}" stroke="#334155" stroke-width="1.2" />
            <text x="0" y="4.5" font-size="11" font-weight="600" fill="{text_color}" text-anchor="middle">{html.escape(text)}</text>
        </g>'''

    # Restaurant -> Menu
    lines.append(make_path([(950, 250), (410, 250), (410, 310)], stroke="#10b981", marker_end="arrow-emerald"))
    lines.append(make_label(600, 250, "delegates menu ops", bg="#064e3b", text_color="#a7f3d0"))

    # Restaurant -> ReservationManager
    lines.append(make_path([(1080, 535), (1080, 550), (700, 550), (700, 560)], stroke="#f59e0b", marker_end="arrow-amber"))
    lines.append(make_label(850, 550, "delegates booking ops", bg="#78350f", text_color="#fde68a"))

    # Restaurant -> Layout
    lines.append(make_path([(1280, 535), (1280, 560)], stroke="#0ea5e9", marker_end="arrow-sky"))
    lines.append(make_label(1280, 545, "delegates table queries", bg="#0c4a6e", text_color="#bae6fd"))

    # Restaurant -> OrderManager
    lines.append(make_path([(1610, 250), (1680, 250), (1680, 300)], stroke="#ec4899", marker_end="arrow-pink"))
    lines.append(make_label(1645, 235, "enqueues commands", bg="#831843", text_color="#fbcfe8"))

    # Menu -> MenuItem
    lines.append(make_path([(235, 475), (235, 560)], stroke="#10b981", marker_start="diamond-emerald", marker_end="arrow-emerald"))
    lines.append(make_label(235, 515, "1 .. * (by name)", bg="#064e3b", text_color="#a7f3d0"))

    # MenuItem -> Category
    lines.append(make_path([(235, 780), (235, 860)], stroke="#10b981", marker_end="arrow-emerald"))
    lines.append(make_label(235, 820, "has category", bg="#0f172a"))

    # Layout -> Table
    lines.append(make_path([(1195, 790), (1195, 860)], stroke="#0ea5e9", marker_start="diamond-sky", marker_end="arrow-sky"))
    lines.append(make_label(1195, 825, "organizes 1 .. *", bg="#0c4a6e", text_color="#bae6fd"))

    # ReservationManager -> Layout
    lines.append(make_path([(910, 660), (970, 660)], stroke="#f59e0b", marker_end="arrow-amber"))
    lines.append(make_label(940, 645, "queries", bg="#78350f", text_color="#fde68a"))

    # ReservationManager -> Reservation
    lines.append(make_path([(700, 810), (700, 880)], stroke="#f59e0b", marker_start="diamond-amber", marker_end="arrow-amber"))
    lines.append(make_label(700, 845, "owns Set<Reservation>", bg="#78350f", text_color="#fde68a"))

    # Reservation -> Table
    lines.append(make_path([(910, 985), (970, 985)], stroke="#f59e0b", marker_end="arrow-sky"))
    lines.append(make_label(940, 970, "assignedTable", bg="#0f172a"))

    # Table -> OrderItem
    lines.append(make_path([(1420, 1060), (1680, 1060)], stroke="#0ea5e9", marker_start="diamond-sky", marker_end="arrow-sky"))
    lines.append(make_label(1550, 1045, "orderedItems: Map<MenuItem, List<OrderItem>>", bg="#0c4a6e", text_color="#bae6fd"))

    # OrderManager -> OrderCommand
    lines.append(make_path([(1880, 475), (1880, 560)], stroke="#ec4899", marker_start="diamond-pink", marker_end="arrow-pink"))
    lines.append(make_label(1880, 515, "queues FIFO", bg="#831843", text_color="#fbcfe8"))

    # Realizations: Concrete Commands -> OrderCommand
    lines.append(make_path([(1670, 770), (1670, 720), (1780, 720), (1780, 700)], stroke="#ec4899", dashed=True, marker_end="realization"))
    lines.append(make_path([(2020, 770), (2020, 720), (1880, 720), (1880, 700)], stroke="#ec4899", dashed=True, marker_end="realization"))
    lines.append(make_path([(2370, 770), (2370, 720), (1980, 720), (1980, 700)], stroke="#ec4899", dashed=True, marker_end="realization"))
    lines.append(make_label(1880, 720, "implements", bg="#831843", text_color="#fbcfe8"))

    # Commands -> OrderItem
    lines.append(make_path([(1670, 910), (1670, 950), (1750, 950), (1750, 990)], stroke="#c084fc", marker_end="arrow-purple"))
    lines.append(make_path([(2020, 910), (2020, 950), (1890, 950), (1890, 990)], stroke="#c084fc", marker_end="arrow-purple"))
    lines.append(make_path([(2370, 910), (2370, 950), (2030, 950), (2030, 990)], stroke="#c084fc", marker_end="arrow-purple"))
    lines.append(make_label(1890, 950, "invokes lifecycle methods on receiver", bg="#581c87", text_color="#e9d5ff"))

    # OrderItem -> MenuItem
    lines.append(make_path([(1680, 1180), (410, 1180), (410, 680)], stroke="#10b981", marker_end="arrow-emerald"))
    lines.append(make_label(1050, 1180, "references MenuItem (immutable)", bg="#064e3b", text_color="#a7f3d0"))

    # OrderItem -> Status
    lines.append(make_path([(2100, 1075), (2170, 1075)], stroke="#ec4899", marker_end="arrow-pink"))
    lines.append(make_label(2135, 1060, "status", bg="#0f172a"))

    svg.extend(lines)

    # -------------------------------------------------------------
    # BOTTOM ARCHITECTURE & CONCURRENCY FOOTER PANEL
    # -------------------------------------------------------------
    svg.append('''<g transform="translate(60, 1470)">
        <rect x="0" y="0" width="2480" height="140" rx="12" fill="#111827" stroke="#1e293b" stroke-width="1.8" class="shadow" />
        
        <g transform="translate(30, 22)">
            <rect x="0" y="0" width="460" height="96" rx="8" fill="#1e1b4b" stroke="#3730a3" stroke-width="1.2" />
            <text x="22" y="28" font-size="13.5" font-weight="700" fill="#a5b4fc">1. FACADE PATTERN (Restaurant)</text>
            <text x="22" y="52" font-size="11.5" fill="#cbd5e1">• Central simplified front door into subsystems</text>
            <text x="22" y="72" font-size="11.5" fill="#cbd5e1">• Completely stateless: owns NO internal maps/sets</text>
            <text x="22" y="92" font-size="11.5" fill="#cbd5e1">• Pure delegation to Domain Managers &amp; Tables</text>
        </g>

        <g transform="translate(520, 22)">
            <rect x="0" y="0" width="580" height="96" rx="8" fill="#831843" stroke="#9d174d" stroke-width="1.2" />
            <text x="22" y="28" font-size="13.5" font-weight="700" fill="#f472b6">2. COMMAND PATTERN (Order Lifecycle)</text>
            <text x="22" y="52" font-size="11.5" fill="#cbd5e1">• Encapsulates state actions: SendToKitchen, Deliver, Cancel</text>
            <text x="22" y="72" font-size="11.5" fill="#cbd5e1">• Invoker: OrderManager queues and drains commands FIFO</text>
            <text x="22" y="92" font-size="11.5" fill="#cbd5e1">• Receiver: OrderItem enforces state transition guards</text>
        </g>

        <g transform="translate(1130, 22)">
            <rect x="0" y="0" width="760" height="96" rx="8" fill="#064e3b" stroke="#047857" stroke-width="1.2" />
            <text x="22" y="28" font-size="13.5" font-weight="700" fill="#34d399">3. SECTION 9 CONCURRENCY &amp; RACE CONDITION BLUEPRINT</text>
            <text x="22" y="52" font-size="11.5" fill="#cbd5e1">• Table.reserveIfAvailable: putIfAbsent eliminates check-then-act double-booking</text>
            <text x="22" y="72" font-size="11.5" fill="#cbd5e1">• Table Orders: ConcurrentHashMap + atomic compute/computeIfPresent per MenuItem</text>
            <text x="22" y="92" font-size="11.5" fill="#cbd5e1">• OrderManager: ConcurrentLinkedQueue with atomic poll() drain loop</text>
        </g>

        <g transform="translate(1920, 22)">
            <rect x="0" y="0" width="530" height="96" rx="8" fill="#1e293b" stroke="#334155" stroke-width="1.2" />
            <text x="22" y="28" font-size="13.5" font-weight="700" fill="#f8fafc">4. UML NOTATION LEGEND</text>
            <text x="22" y="52" font-size="11.5" fill="#94a3b8">──◆ Composition / Aggregate Ownership</text>
            <text x="22" y="72" font-size="11.5" fill="#94a3b8">──► Directed Association / Delegation</text>
            <text x="22" y="92" font-size="11.5" fill="#94a3b8">- - ▷ Interface Realization / Implementation</text>
        </g>
    </g>''')

    svg.append('</svg>')
    return "\n".join(svg)

if __name__ == "__main__":
    content = create_svg()
    with open("restaurant-lld-class-diagram.svg", "w", encoding="utf-8") as f:
        f.write(content)
    print("SVG generated successfully. Size:", len(content), "bytes")
