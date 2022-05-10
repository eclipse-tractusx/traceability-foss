import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-sidebar-section',
  templateUrl: './sidebar-section.component.html',
  styleUrls: ['./sidebar-section.component.scss'],
})
export class SidebarSectionComponent implements OnInit {
  @Input() name: string;
  @Input() iconName: string;
  @Input() isActive: boolean;
  @Input() isExpanded: boolean;

  @Output() click = new EventEmitter<void>();

  constructor() {}

  ngOnInit(): void {}
}
